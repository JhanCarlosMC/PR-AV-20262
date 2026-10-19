package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

/** Excepción de aplicación: el identificador no existe (Guía 06) */
public class FolioNoEncontradoException extends RuntimeException {
    public FolioNoEncontradoException(CodigoReserva reserva) {
        super("No se encontró el folio de la reserva: " + reserva.valor());
    }
}
