package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

// L-09: el valor lo define cada equipo, nunca se quema en el código
public record UmbralEdadFacturable(int anios) {

    public UmbralEdadFacturable {
        if (anios < 0 || anios > 30) {
            throw new ReglaDominioException("El umbral de edad facturable debe estar entre 0 y 30 años.");
        }
    }
}
