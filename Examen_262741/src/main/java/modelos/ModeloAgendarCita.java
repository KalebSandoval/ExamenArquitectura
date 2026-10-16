package modelos;

import entidades.CitaMedica;
import entidades.DiaDeLaSemana;
import entidades.HorarioConsulta;
import entidades.Medico;
import entidades.Paciente;
import interfaces.IModelo;
import interfaces.IObservado;
import interfaces.IObservador;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ModeloAgendarCita implements IObservado, IModelo {

    /**
     * Cuántos días hacia adelante se busca un espacio libre antes de rendirse.
     */
    private static final int DIAS_DE_BUSQUEDA = 90;

    private final List<IObservador> observadores = new ArrayList<>();

    // "Base de datos" mock
    private final List<Paciente> pacientes = new ArrayList<>();
    private final List<Medico> medicos = new ArrayList<>();
    private final List<CitaMedica> citas = new ArrayList<>();

    // Estado actual (lo que lee la Pantalla)
    private Paciente paciente;
    private Medico medicoSeleccionado;
    private CitaMedica citaAgendada;
    private String mensaje = "";

    public ModeloAgendarCita() {
        cargarDatosMock();
    }

    // ---------------- Base de datos mock ----------------
    private void cargarDatosMock() {
        pacientes.add(new Paciente("Juan Pérez López", "12345678901"));
        pacientes.add(new Paciente("María Gómez Ruiz", "98765432109"));
        pacientes.add(new Paciente("Carlos Ramírez Soto", "11223344556"));

        medicos.add(new Medico("Dra. Ana Torres", "Cardiología", "Consultorio 101",
                new HorarioConsulta(List.of(DiaDeLaSemana.LUNES, DiaDeLaSemana.MIERCOLES, DiaDeLaSemana.VIERNES),
                        LocalTime.of(8, 0), LocalTime.of(12, 0))));
        medicos.add(new Medico("Dr. Luis Herrera", "Pediatría", "Consultorio 202",
                new HorarioConsulta(List.of(DiaDeLaSemana.MARTES, DiaDeLaSemana.JUEVES),
                        LocalTime.of(9, 0), LocalTime.of(14, 0))));
        medicos.add(new Medico("Dra. Sofía Mendoza", "Dermatología", "Consultorio 305",
                new HorarioConsulta(List.of(DiaDeLaSemana.LUNES, DiaDeLaSemana.MARTES, DiaDeLaSemana.MIERCOLES,
                        DiaDeLaSemana.JUEVES, DiaDeLaSemana.VIERNES),
                        LocalTime.of(10, 0), LocalTime.of(13, 0))));

        medicos.add(new Medico("Dr. Ricardo Salinas", "Medicina General", "Consultorio 110",
                new HorarioConsulta(List.of(DiaDeLaSemana.SABADO), LocalTime.of(9, 0), LocalTime.of(11, 0))));

        // Citas ya existentes: la Dra. Torres tiene sus próximos 5 espacios ocupados,
        // así se puede probar el salto automático al siguiente día disponible.
        Medico torres = medicos.get(0);
        for (int i = 0; i < 5; i++) {
            LocalDateTime libre = buscarSiguienteHorarioLibre(torres);
            citas.add(new CitaMedica(libre, torres, pacientes.get(1)));
        }

        // El Dr. Salinas tiene TODA su agenda ocupada (para probar "sin turnos libres").
        Medico salinas = medicos.get(3);
        LocalDateTime libre;
        while ((libre = buscarSiguienteHorarioLibre(salinas)) != null) {
            citas.add(new CitaMedica(libre, salinas, pacientes.get(2)));
        }
    }

    // ---------------- IObservado ----------------
    @Override
    public void agregarObservador(IObservador observador) {
        observadores.add(observador);
    }

    @Override
    public void notificarObservadores() {
        for (IObservador o : observadores) {
            o.actualizar();
        }
    }

    // ---------------- Lógica de negocio ----------------
    public Paciente buscarPaciente(String nss) {
        medicoSeleccionado = null;
        citaAgendada = null;
        paciente = pacientes.stream()
                .filter(p -> p.getNss().equals(nss))
                .findFirst()
                .orElse(null);
        mensaje = (paciente == null)
                ? "No se encontró ningún paciente con el NSS " + nss + "."
                : "Paciente verificado correctamente.";
        notificarObservadores();
        return paciente;
    }

    @Override
    public Paciente getPaciente() {
        return paciente;
    }

    @Override
    public List<Medico> obtenerMedicos() {
        return medicos;
    }

    /**
     * Si el médico no tiene turnos libres, se avisa y no queda seleccionado.
     * @param medico
     */
    public void setMedicoSeleccionado(Medico medico) {
        if (medico != null && buscarSiguienteHorarioLibre(medico) == null) {
            this.medicoSeleccionado = null;
            this.mensaje = "ALERTA: " + medico.getNombreCompleto()
                    + " no tiene turnos libres. Seleccione otro médico.";
        } else {
            this.medicoSeleccionado = medico;
            this.mensaje = "";
        }
        notificarObservadores();
    }

    public Medico getMedicoSeleccionado() {
        return medicoSeleccionado;
    }

    public boolean agendarCitaMedica(Medico medico, Paciente paciente) {
        LocalDateTime fechaHora = buscarSiguienteHorarioLibre(medico);
        if (fechaHora == null) {
            mensaje = "ERROR: no fue posible agendar la cita (sin disponibilidad). "
                    + "Puede intentar de nuevo o seleccionar otro médico.";
            notificarObservadores();
            return false;
        }
        citaAgendada = new CitaMedica(fechaHora, medico, paciente);
        citas.add(citaAgendada);
        mensaje = "¡Cita agendada con éxito!";
        notificarObservadores();
        return true;
    }

    /**
     * Las citas se generan automáticamente cada hora dentro del horario del
     * médico. Se revisa día por día (empezando hoy); si un día ya no tiene
     * espacio, pasa al siguiente.
     */
    private LocalDateTime buscarSiguienteHorarioLibre(Medico medico) {
        LocalDateTime ahora = LocalDateTime.now();
        HorarioConsulta h = medico.getHorario();

        for (int d = 0; d < DIAS_DE_BUSQUEDA; d++) {
            LocalDate fecha = LocalDate.now().plusDays(d);
            DiaDeLaSemana dia = DiaDeLaSemana.values()[fecha.getDayOfWeek().getValue() - 1];
            if (!h.atiende(dia)) {
                continue;
            }

            for (LocalTime hora = h.getHoraInicio(); hora.isBefore(h.getHoraFin()); hora = hora.plusHours(1)) {
                LocalDateTime candidato = fecha.atTime(hora);
                if (candidato.isAfter(ahora) && !estaOcupado(medico, candidato)) {
                    return candidato;
                }
                if (hora.getHour() == 23) {
                    break; // evita dar la vuelta a medianoche
                }
            }
        }
        return null;
    }

    private boolean estaOcupado(Medico medico, LocalDateTime fechaHora) {
        return citas.stream().anyMatch(c -> c.getMedico() == medico && c.getFechaHora().equals(fechaHora));
    }

    // ---------------- IModelo ----------------
    @Override
    public CitaMedica getCitaAgendada() {
        return citaAgendada;
    }

    @Override
    public Medico getMedico() {
        return medicoSeleccionado;
    }

    @Override
    public String getMensaje() {
        return mensaje;
    }
}
