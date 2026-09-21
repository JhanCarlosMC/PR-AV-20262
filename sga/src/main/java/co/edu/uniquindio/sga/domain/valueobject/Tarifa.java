package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

// RN-05 y F-05: la tarifa es el valor por ocupante facturable por noche, no el precio de la noche.
// Vive dentro del agregado Apartamento y referencia la temporada por identificador.
public record Tarifa(IdTemporada temporada, Dinero valorPorOcupanteNoche) {

    public Tarifa {
        if (temporada == null) {
            throw new ReglaDominioException("La tarifa debe indicar la temporada a la que aplica.");
        }
        if (valorPorOcupanteNoche == null || valorPorOcupanteNoche.esCero()) {
            throw new ReglaDominioException("El valor de la tarifa debe ser mayor que cero.");
        }
    }
}
