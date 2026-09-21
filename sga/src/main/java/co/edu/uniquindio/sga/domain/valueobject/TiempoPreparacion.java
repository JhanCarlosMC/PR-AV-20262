package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

// RN-20: tiempo mínimo entre la salida de un grupo y la entrada del siguiente
// L-13: el valor lo define cada equipo, nunca se quema en el código
public record TiempoPreparacion(int horas) {

    public TiempoPreparacion {
        if (horas < 0) {
            throw new ReglaDominioException("El tiempo de preparación no puede ser negativo.");
        }
    }
}
