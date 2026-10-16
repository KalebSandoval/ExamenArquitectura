package controladores;

import entidades.Medico;
import entidades.Paciente;
import modelos.ModeloAgendarCita;

public class ControladorAgendarCita {

    private final ModeloAgendarCita modelo;

    public ControladorAgendarCita(ModeloAgendarCita modelo) {
        this.modelo = modelo;
    }

    public void buscarPacientePorNss(String nss) {
        modelo.buscarPaciente(nss);
    }

    public void seleccionarMedico(Medico medico) {
        modelo.setMedicoSeleccionado(medico);
    }

    public void agendarCita(Medico medico, Paciente paciente) {
        modelo.agendarCitaMedica(medico, paciente);
    }
}
