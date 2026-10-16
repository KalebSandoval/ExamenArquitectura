package pantallas;

import entidades.CitaMedica;
import entidades.Medico;
import entidades.Paciente;
import interfaces.IModelo;
import modelos.ModeloAgendarCita;
import controladores.ControladorAgendarCita;
import interfaces.IObservado;
import interfaces.IObservador;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Vista por consola (y punto de entrada del programa).
 */
public class PantallaRegistrarCita implements IObservador {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IObservado observado;
    private final IModelo modelo;
    private final ControladorAgendarCita controlador;
    private final Scanner scanner = new Scanner(System.in);
    private boolean nssMostrado = false;

    public PantallaRegistrarCita(IObservado observado, IModelo modelo, ControladorAgendarCita controlador) {
        this.observado = observado;
        this.modelo = modelo;
        this.controlador = controlador;
        this.observado.agregarObservador(this); // la pantalla se suscribe al modelo
    }

    // ---------------- Flujo principal (entradas del usuario) ----------------
    public void iniciar() {
        System.out.println("=== REGISTRAR CITA MÉDICA ===");

        // 1) Pedir NSS hasta encontrar un paciente
        while (modelo.getPaciente() == null) {
            System.out.print("\nIngrese el NSS del paciente (o 'salir'): ");
            String nss = scanner.nextLine().trim();
            if (nss.equalsIgnoreCase("salir")) {
                return;
            }
            controlador.buscarPacientePorNss(nss); // el modelo notifica -> actualizar()
        }

        // 2) Seleccionar / cambiar de médico y agendar
        while (modelo.getCitaAgendada() == null) {
            boolean hayMedico = modelo.getMedico() != null;
            System.out.print(hayMedico
                    ? "\nNúmero de otro médico para cambiar, o 'A' para agendar cita: "
                    : "\nNúmero del médico: ");
            String entrada = scanner.nextLine().trim();

            if (hayMedico && entrada.equalsIgnoreCase("A")) {
                controlador.agendarCita(modelo.getMedico(), modelo.getPaciente());
                continue;
            }
            try {
                int n = Integer.parseInt(entrada);
                List<Medico> medicos = modelo.obtenerMedicos();
                if (n >= 1 && n <= medicos.size()) {
                    controlador.seleccionarMedico(medicos.get(n - 1));
                    continue;
                }
            } catch (NumberFormatException ignorada) {
            }
            System.out.println("Opción inválida.");
        }
    }

    // ---------------- IObservador ----------------
    @Override
    public void actualizar() {
        mostrarMensaje();

        if (modelo.getPaciente() == null) {
            return;
        }

        if (modelo.getCitaAgendada() != null) {
            mostrarCita();
            return;
        }

        if (!nssMostrado) {
            mostrarNss();
            nssMostrado = true;
        }
        mostrarMedico();
    }

    // ---------------- Métodos de dibujo ----------------
    public void mostrarMensaje() {
        if (!modelo.getMensaje().isEmpty()) {
            System.out.println("\n>> " + modelo.getMensaje());
        }
    }

    /**
     * Datos generales del paciente.
     */
    public void mostrarNss() {
        Paciente p = modelo.getPaciente();
        System.out.println("\n--- DATOS DEL PACIENTE ---");
        System.out.println("Nombre: " + p.getNombreCompleto());
        System.out.println("NSS:    " + p.getNss());
    }

    /**
     * Lista de médicos (siempre visible) y, si hay uno seleccionado, su panel
     * de datos.
     */
    public void mostrarMedico() {
        System.out.println("\n--- MÉDICOS DISPONIBLES ---");
        List<Medico> medicos = modelo.obtenerMedicos();
        for (int i = 0; i < medicos.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1,
                    medicos.get(i).getNombreCompleto(), medicos.get(i).getEspecialidad());
        }

        Medico m = modelo.getMedico();
        if (m != null) {
            System.out.println("\n  +---------- DATOS DEL MÉDICO ----------");
            System.out.println("  | Nombre:       " + m.getNombreCompleto());
            System.out.println("  | Especialidad: " + m.getEspecialidad());
            System.out.println("  | Consultorio:  " + m.getConsultorio());
            System.out.println("  | Consulta:     " + m.getHorario());
            System.out.println("  +--------------------------------------");
        }
    }

    public void mostrarCita() {
        CitaMedica c = modelo.getCitaAgendada();
        System.out.println("\n=== CONFIRMACIÓN DE CITA ===");
        System.out.println("Paciente:     " + c.getPaciente().getNombreCompleto()
                + " (NSS " + c.getPaciente().getNss() + ")");
        System.out.println("Médico:       " + c.getMedico().getNombreCompleto());
        System.out.println("Consultorio:  " + c.getMedico().getConsultorio());
        System.out.println("Fecha y hora: " + c.getFechaHora().format(FORMATO));
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        ModeloAgendarCita modelo = new ModeloAgendarCita();
        ControladorAgendarCita controlador = new ControladorAgendarCita(modelo);
        PantallaRegistrarCita pantalla = new PantallaRegistrarCita(modelo, modelo, controlador);
        pantalla.iniciar();
    }
}
