package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

/** Excepción de aplicación: el identificador no existe (Guía 06) */
public class ReservaNoEncontradaException extends RuntimeException {
    public ReservaNoEncontradaException(CodigoReserva codigo) {
        super("No se encontró la reserva con código: " + codigo.valor());
    }
}
