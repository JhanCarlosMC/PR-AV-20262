package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

public record CodigoFolio(String valor) {

    public CodigoFolio {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El código del folio es obligatorio.");
        }
        valor = valor.trim();
    }
}
