package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalTime;

// L-15: hora del día de entrada a partir de la cual puede declararse el no-show.
// El valor lo define cada equipo, nunca se quema en el código
public record HoraLimiteNoShow(LocalTime hora) {

    public HoraLimiteNoShow {
        if (hora == null) {
            throw new ReglaDominioException("La hora límite para declarar no-show es obligatoria.");
        }
    }
}
