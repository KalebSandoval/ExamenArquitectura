package entidades;

public class Medico {

    private final String nombreCompleto;
    private final String especialidad;
    private final String consultorio;
    private final HorarioConsulta horario; // composición 1 a 1

    public Medico(String nombreCompleto, String especialidad, String consultorio, HorarioConsulta horario) {
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
        this.consultorio = consultorio;
        this.horario = horario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getConsultorio() {
        return consultorio;
    }

    public HorarioConsulta getHorario() {
        return horario;
    }
}
