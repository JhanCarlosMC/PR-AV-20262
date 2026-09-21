package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

// RN-19: la combinación canal + identificador externo es única
public record IdentificadorExterno(CanalOrigen canal, String valor) {

    public IdentificadorExterno {
        if (canal == null) {
            throw new ReglaDominioException("El identificador externo debe indicar el canal de origen.");
        }
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador externo no puede estar vacío.");
        }
        valor = valor.trim();
    }
}
