package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

/** Objeto de valor (Guía 05) */
public record IdNovedad(String valor) {

    public IdNovedad {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador de la novedad es obligatorio.");
        }
        valor = valor.trim();
    }
}
