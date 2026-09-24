package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

public class ReservaNoEncontradadException extends RuntimeException {
    public ReservaNoEncontradadException(CodigoReserva codigo) {
        super("No se encontro la reserva con codigo: " + codigo);
    }
}
