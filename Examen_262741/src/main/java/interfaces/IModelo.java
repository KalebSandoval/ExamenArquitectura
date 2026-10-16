package interfaces;

import entidades.CitaMedica;
import entidades.Medico;
import entidades.Paciente;
import java.util.List;

/**
 * Vista de solo lectura del modelo, para que la Pantalla consulte su estado.
 */
public interface IModelo {

    CitaMedica getCitaAgendada();

    Paciente getPaciente();

    Medico getMedico();

    String getMensaje();

    // Agregado al diagrama original: la Pantalla necesita la lista para mostrarla.
    List<Medico> obtenerMedicos();
}
