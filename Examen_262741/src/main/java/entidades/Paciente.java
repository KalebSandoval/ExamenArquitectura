package entidades;

public class Paciente {

    private final String nombreCompleto;
    private final String nss;

    public Paciente(String nombreCompleto, String nss) {
        this.nombreCompleto = nombreCompleto;
        this.nss = nss;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getNss() {
        return nss;
    }
}
