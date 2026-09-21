package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;

// RN-13 y RN-22: es la versión de política que queda congelada en la reserva al crearla
public record VersionPolitica(int numero, LocalDate vigenteDesde) {

    public VersionPolitica {
        if (numero <= 0) {
            throw new ReglaDominioException("El número de versión de la política debe ser positivo.");
        }
        if (vigenteDesde == null) {
            throw new ReglaDominioException("La versión de la política debe indicar desde cuándo rige.");
        }
    }
}
