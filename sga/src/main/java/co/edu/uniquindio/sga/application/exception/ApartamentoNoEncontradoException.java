package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

/** Excepción de aplicación: el identificador no existe (Guía 06) */
public class ApartamentoNoEncontradoException extends RuntimeException {
    public ApartamentoNoEncontradoException(IdentificacionApartamento identificacion) {
        super("No se encontró el apartamento con identificación: " + identificacion.valor());
    }
}
