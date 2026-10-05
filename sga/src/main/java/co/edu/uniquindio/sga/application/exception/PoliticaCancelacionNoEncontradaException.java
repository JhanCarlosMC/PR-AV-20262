package co.edu.uniquindio.sga.application.exception;

// El alojamiento no tiene configurada la versión de política que se necesita (F-07)
public class PoliticaCancelacionNoEncontradaException extends RuntimeException {
    public PoliticaCancelacionNoEncontradaException(String detalle) {
        super("No se encontró la política de cancelación: " + detalle);
    }
}
