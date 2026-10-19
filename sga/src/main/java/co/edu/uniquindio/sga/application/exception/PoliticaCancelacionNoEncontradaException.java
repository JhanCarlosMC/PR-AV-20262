package co.edu.uniquindio.sga.application.exception;

// El alojamiento no tiene configurada la versión de política que se necesita (F-07)
/** Excepción de aplicación: el identificador no existe (Guía 06) */
public class PoliticaCancelacionNoEncontradaException extends RuntimeException {
    public PoliticaCancelacionNoEncontradaException(String detalle) {
        super("No se encontró la política de cancelación: " + detalle);
    }
}
