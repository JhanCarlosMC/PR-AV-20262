package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalTime;

// RN-09: toda reserva debe registrar hora estimada de llegada antes de confirmarse
public record HoraEstimadaLlegada(LocalTime hora) {

    public HoraEstimadaLlegada {
        if (hora == null) {
            throw new ReglaDominioException("La hora estimada de llegada es obligatoria.");
        }
    }
}
