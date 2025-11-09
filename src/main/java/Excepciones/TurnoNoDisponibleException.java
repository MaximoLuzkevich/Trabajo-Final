package Excepciones;

public class TurnoNoDisponibleException extends RuntimeException {
    public TurnoNoDisponibleException(String message) {
        super(message);
    }
}
