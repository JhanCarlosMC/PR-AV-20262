package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;

/**
 * Rango cerrado en ambos extremos ([desde, hasta]), usado por {@code Temporada} y
 * {@code Bloqueo}. Distinto de {@link Estancia}, que es semiabierto: confundirlos
 * produce errores de una noche en el cálculo de disponibilidad y de valor.
 */
public record Periodo(LocalDate desde, LocalDate hasta) {

    public Periodo {
        if (desde == null || hasta == null) {
            throw new ReglaDominioException("El periodo debe tener fecha de inicio y de fin.");
        }
        if (hasta.isBefore(desde)) {
            throw new ReglaDominioException("La fecha final del periodo no puede ser anterior a la inicial.");
        }
    }

    public boolean contiene(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    public boolean seSolapaCon(Periodo otro) {
        return !desde.isAfter(otro.hasta) && !otro.desde.isAfter(hasta);
    }
}
