package interfaces;

/**
 * Quien notifica los cambios (el Modelo).
 */
public interface IObservado {

    void agregarObservador(IObservador observador);

    void notificarObservadores();
}
