package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.Cargo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoFolio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Raíz del agregado Folio.
 *
 * <p>RN-16: los cargos y pagos no se modifican ni se eliminan; toda corrección es un
 * movimiento inverso. No existen métodos para eliminar un cargo ni para modificar un pago.
 */
public class Folio {

    private final CodigoFolio codigo;
    private final CodigoReserva reserva;
    // RN de oro #9: colección interna siempre de solo lectura; toda modificación reemplaza la referencia
    private List<Cargo> cargos = List.of();
    private List<Pago> pagos = List.of();
    private boolean cerrado;
    private String autorizacionCierre;

    public Folio(CodigoFolio codigo, CodigoReserva reserva) {
        if (codigo == null) {
            throw new ReglaDominioException("El folio debe tener un código.");
        }
        if (reserva == null) {
            throw new ReglaDominioException("El folio debe indicar la reserva a la que pertenece.");
        }
        this.codigo = codigo;
        this.reserva = reserva;
        this.cerrado = false;
    }

    /** RN-16: un cargo solo se agrega, nunca se modifica ni se elimina. */
    public void registrarCargo(Cargo cargo) {
        if (cargo == null) {
            throw new ReglaDominioException("El cargo a registrar es obligatorio.");
        }
        if (cerrado) {
            throw new ReglaDominioException("No se pueden registrar cargos en un folio cerrado.");
        }
        if (cargo.esInverso()) {
            throw new ReglaDominioException("Un movimiento inverso se registra con registrarMovimientoInverso.");
        }
        cargos = agregarCargo(cargo);
    }

    /** RN-16: la corrección es un cargo nuevo marcado como inverso, jamás una edición. */
    public void registrarMovimientoInverso(Cargo inverso) {
        if (inverso == null) {
            throw new ReglaDominioException("El movimiento inverso a registrar es obligatorio.");
        }
        if (cerrado) {
            throw new ReglaDominioException("No se pueden registrar movimientos en un folio cerrado.");
        }
        if (!inverso.esInverso()) {
            throw new ReglaDominioException("El movimiento debe estar marcado como inverso.");
        }
        cargos = agregarCargo(inverso);
    }

    /** RN-15: todo pago se registra contra el folio con su medio y su fecha. */
    public void registrarPago(Pago pago) {
        if (pago == null) {
            throw new ReglaDominioException("El pago a registrar es obligatorio.");
        }
        if (cerrado) {
            throw new ReglaDominioException("No se pueden registrar pagos en un folio cerrado.");
        }
        pagos = agregarPago(pago);
    }

    private List<Cargo> agregarCargo(Cargo cargo) {
        List<Cargo> actualizados = new ArrayList<>(cargos);
        actualizados.add(cargo);
        return List.copyOf(actualizados);
    }

    private List<Pago> agregarPago(Pago pago) {
        List<Pago> actualizados = new ArrayList<>(pagos);
        actualizados.add(pago);
        return List.copyOf(actualizados);
    }

    public Dinero totalCargos() {
        BigDecimal total = cargos.stream()
                .map(cargo -> cargo.esInverso() ? cargo.valor().valor().negate() : cargo.valor().valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dinero(total);
    }

    public Dinero totalPagos() {
        BigDecimal total = pagos.stream()
                .map(pago -> pago.getMonto().valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dinero(total);
    }

    // RN-15: el saldo es la diferencia entre cargos y pagos; valor derivado, nunca almacenado
    public Dinero saldo() {
        return totalCargos().restar(totalPagos());
    }

    /** RN-17: el folio no puede cerrarse con saldo distinto de cero sin autorización explícita. */
    public boolean puedeCerrarse() {
        return saldo().esCero();
    }

    public void cerrar(String autorizacion) {
        if (cerrado) {
            throw new ReglaDominioException("El folio ya está cerrado.");
        }
        // RN-17: con saldo distinto de cero se exige autorización explícita registrada
        if (!saldo().esCero()) {
            if (autorizacion == null || autorizacion.isBlank()) {
                throw new ReglaDominioException(
                        "El folio no puede cerrarse con saldo distinto de cero sin autorización explícita.");
            }
            this.autorizacionCierre = autorizacion;
        }
        this.cerrado = true;
    }

    public CodigoFolio getCodigo() {
        return codigo;
    }

    public CodigoReserva getReserva() {
        return reserva;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public boolean isCerrado() {
        return cerrado;
    }

    public String getAutorizacionCierre() {
        return autorizacionCierre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Folio folio)) return false;
        return Objects.equals(codigo, folio.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
