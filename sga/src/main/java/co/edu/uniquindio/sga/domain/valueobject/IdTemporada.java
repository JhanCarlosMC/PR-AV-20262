package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

public record IdTemporada(String valor) {

    public IdTemporada {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador de la temporada es obligatorio.");
        }
        valor = valor.trim();
    }
}
