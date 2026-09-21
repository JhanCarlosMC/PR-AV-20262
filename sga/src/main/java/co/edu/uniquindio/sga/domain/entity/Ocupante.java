package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.DocumentoIdentidad;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Entidad interna del agregado Reserva: no existe fuera de una reserva y no tiene
 * repositorio propio.
 */
public class Ocupante {

    private final DocumentoIdentidad documento;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;

    public Ocupante(DocumentoIdentidad documento, String nombreCompleto, LocalDate fechaNacimiento) {
        if (documento == null) {
            throw new ReglaDominioException("El ocupante debe tener un documento de identidad.");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new ReglaDominioException("El ocupante debe tener un nombre completo.");
        }
        if (fechaNacimiento == null) {
            throw new ReglaDominioException("El ocupante debe tener fecha de nacimiento.");
        }
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
    }

    // La edad se calcula, nunca se almacena
    public int edadA(LocalDate fecha) {
        return Period.between(fechaNacimiento, fecha).getYears();
    }

    /** RN-06: un ocupante es facturable si a la fecha de entrada alcanza el umbral configurado. */
    public boolean esFacturableEn(Estancia estancia, UmbralEdadFacturable umbral) {
        return edadA(estancia.fechaEntrada()) >= umbral.anios();
    }

    public DocumentoIdentidad getDocumento() {
        return documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ocupante ocupante)) return false;
        return Objects.equals(documento, ocupante.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(documento);
    }
}
