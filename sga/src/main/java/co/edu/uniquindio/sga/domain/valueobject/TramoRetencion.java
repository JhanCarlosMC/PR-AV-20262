package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

// RN-13: determina la retención por cancelación según la antelación con la que ocurre
// L-10: el equipo define los tramos, con un mínimo de dos
public record TramoRetencion(int horasAntelacionMinima, Porcentaje retencion) {

    public TramoRetencion {
        if (horasAntelacionMinima < 0) {
            throw new ReglaDominioException("La antelación mínima del tramo no puede ser negativa.");
        }
        if (retencion == null) {
            throw new ReglaDominioException("El tramo de retención debe indicar el porcentaje a retener.");
        }
    }
}
