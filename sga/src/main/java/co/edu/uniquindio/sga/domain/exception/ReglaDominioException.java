package co.edu.uniquindio.sga.domain.exception;

/** Excepción del dominio: una regla del negocio se violó (Guía 04) */
public class ReglaDominioException extends RuntimeException {

    public ReglaDominioException(String message) {
        super(message);
    }
}
