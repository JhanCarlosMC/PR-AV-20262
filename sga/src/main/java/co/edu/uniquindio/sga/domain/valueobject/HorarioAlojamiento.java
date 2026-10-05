package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalTime;

// L-12: horas de entrada y salida del alojamiento. La antelación de una cancelación
// (RN-13) se mide contra la hora de entrada del día de entrada.
public record HorarioAlojamiento(LocalTime horaEntrada, LocalTime horaSalida) {

    public HorarioAlojamiento {
        if (horaEntrada == null || horaSalida == null) {
            throw new ReglaDominioException("El alojamiento debe definir hora de entrada y hora de salida.");
        }
    }
}
