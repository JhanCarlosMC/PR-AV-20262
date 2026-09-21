package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

// RN-21: una reserva PENDIENTE que supera este plazo se cancela automáticamente
// L-14: el valor lo define cada equipo, nunca se quema en el código
public record PlazoConfirmacion(int horas) {

    public PlazoConfirmacion {
        if (horas <= 0) {
            throw new ReglaDominioException("El plazo de confirmación debe ser mayor que cero.");
        }
    }
}
