package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

/** Objeto de valor (Guía 05) */
public record IdPago(String valor) {

    public IdPago {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador del pago es obligatorio.");
        }
        valor = valor.trim();
    }
}
