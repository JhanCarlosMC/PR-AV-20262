package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

public record IdBloqueo(String valor) {

    public IdBloqueo {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador del bloqueo es obligatorio.");
        }
        valor = valor.trim();
    }
}
