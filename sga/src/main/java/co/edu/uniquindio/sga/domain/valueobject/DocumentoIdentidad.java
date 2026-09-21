package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

public record DocumentoIdentidad(String valor) {

    public DocumentoIdentidad {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El documento de identidad es obligatorio.");
        }
        valor = valor.trim();
        if (!valor.chars().allMatch(Character::isLetterOrDigit)) {
            throw new ReglaDominioException("El documento de identidad solo admite caracteres alfanuméricos.");
        }
    }
}
