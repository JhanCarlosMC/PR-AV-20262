package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Intervalo cerrado en la entrada y abierto en la salida: [entrada, salida).
 * Distinto de {@link Periodo}, que es cerrado en ambos extremos. Confundirlos
 * produce errores de una noche en el cálculo de disponibilidad y de valor.
 */
public record Estancia(LocalDate fechaEntrada, LocalDate fechaSalida) {

    public Estancia {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new ReglaDominioException("La estancia debe tener fecha de entrada y de salida.");
        }
        // RN-03: la fecha de salida es posterior a la de entrada, toda estancia tiene al menos una noche
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new ReglaDominioException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
    }

    // La noche de salida no se ocupa ni se cobra
    public int noches() {
        return (int) (fechaSalida.toEpochDay() - fechaEntrada.toEpochDay());
    }

    public boolean incluye(LocalDate noche) {
        return !noche.isBefore(fechaEntrada) && noche.isBefore(fechaSalida);
    }

    /** RN-01: un apartamento no puede tener dos reservas activas solapadas. */
    public boolean seSolapaCon(Estancia otra) {
        return fechaEntrada.isBefore(otra.fechaSalida) && otra.fechaEntrada.isBefore(fechaSalida);
    }

    // RN-05: sirve para liquidar noche por noche, aplicando la tarifa vigente en cada una
    public List<LocalDate> nochesOcupadas() {
        List<LocalDate> resultado = new ArrayList<>();
        for (LocalDate noche = fechaEntrada; noche.isBefore(fechaSalida); noche = noche.plusDays(1)) {
            resultado.add(noche);
        }
        return List.copyOf(resultado);
    }
}
