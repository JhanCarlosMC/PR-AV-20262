package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.util.List;

// Desglose noche por noche de una estancia (7.4). Sirve tanto para cotizar como para
// congelar el valor de una reserva al crearla (RN-22).
public record LiquidacionEstancia(List<NocheLiquidada> noches) {

    public LiquidacionEstancia {
        if (noches == null || noches.isEmpty()) {
            throw new ReglaDominioException("La liquidación debe tener al menos una noche.");
        }
        noches = List.copyOf(noches);
    }

    public Dinero total() {
        return noches.stream()
                .map(NocheLiquidada::subtotal)
                .reduce(Dinero.CERO, Dinero::sumar);
    }
}
