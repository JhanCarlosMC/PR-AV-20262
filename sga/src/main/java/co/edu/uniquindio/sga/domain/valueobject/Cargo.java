package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;

// RN-16: un cargo nunca se modifica ni se elimina; esInverso marca el movimiento de corrección.
// Es objeto de valor, no entidad: un cargo no se identifica, se acumula.
public record Cargo(ConceptoCargo concepto, Dinero valor, LocalDate fecha, String descripcion, boolean esInverso) {

    public Cargo {
        if (concepto == null) {
            throw new ReglaDominioException("El cargo debe indicar su concepto.");
        }
        if (valor == null) {
            throw new ReglaDominioException("El cargo debe indicar su valor.");
        }
        if (fecha == null) {
            throw new ReglaDominioException("El cargo debe indicar su fecha.");
        }
    }
}
