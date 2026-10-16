package entidades;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class HorarioConsulta {

    private final List<DiaDeLaSemana> dia;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;

    public HorarioConsulta(List<DiaDeLaSemana> dia, LocalTime horaInicio, LocalTime horaFin) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public List<DiaDeLaSemana> getDia() {
        return dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public boolean atiende(DiaDeLaSemana d) {
        return dia.contains(d);
    }

    @Override
    public String toString() {
        String dias = dia.stream().map(Enum::name).collect(Collectors.joining(", "));
        return dias + " de " + horaInicio + " a " + horaFin;
    }
}
