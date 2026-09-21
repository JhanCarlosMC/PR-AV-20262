package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.IdPago;
import co.edu.uniquindio.sga.domain.valueobject.MedioPago;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad interna del agregado Folio. Inmutable después de creado.
 *
 * <p>RN-16: los pagos no se modifican ni se eliminan; toda corrección es un movimiento inverso.
 */
public class Pago {

    private final IdPago id;
    private final Dinero monto;
    private final MedioPago medio;
    private final LocalDate fecha;

    public Pago(IdPago id, Dinero monto, MedioPago medio, LocalDate fecha) {
        if (id == null) {
            throw new ReglaDominioException("El pago debe tener un identificador.");
        }
        if (monto == null) {
            throw new ReglaDominioException("El pago debe indicar su monto.");
        }
        // RN-15: todo pago se registra contra un folio con su medio y su fecha
        if (medio == null) {
            throw new ReglaDominioException("El pago debe indicar su medio de pago.");
        }
        if (fecha == null) {
            throw new ReglaDominioException("El pago debe indicar su fecha.");
        }
        this.id = id;
        this.monto = monto;
        this.medio = medio;
        this.fecha = fecha;
    }

    public IdPago getId() {
        return id;
    }

    public Dinero getMonto() {
        return monto;
    }

    public MedioPago getMedio() {
        return medio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago pago)) return false;
        return Objects.equals(id, pago.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
