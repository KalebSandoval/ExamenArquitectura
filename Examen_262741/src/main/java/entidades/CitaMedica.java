package entidades;

import java.time.LocalDateTime;

public class CitaMedica {

    private final LocalDateTime fechaHora;
    private final Medico medico;     // +1 médico, 0..N citas
    private final Paciente paciente; // +1 paciente, 0..N citas

    public CitaMedica(LocalDateTime fechaHora, Medico medico, Paciente paciente) {
        this.fechaHora = fechaHora;
        this.medico = medico;
        this.paciente = paciente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Medico getMedico() {
        return medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }
}
