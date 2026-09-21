package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.GravedadNovedad;
import co.edu.uniquindio.sga.domain.valueobject.IdNovedad;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Raíz del agregado Novedad.
 */
public class Novedad {

    private final IdNovedad id;
    private final IdentificacionApartamento apartamento;
    private String descripcion;
    private GravedadNovedad gravedad;
    private final LocalDate fechaReporte;
    private boolean atendida;

    public Novedad(IdNovedad id, IdentificacionApartamento apartamento, String descripcion,
                    GravedadNovedad gravedad, LocalDate fechaReporte) {
        if (id == null) {
            throw new ReglaDominioException("La novedad debe tener un identificador.");
        }
        if (apartamento == null) {
            throw new ReglaDominioException("La novedad debe indicar el apartamento afectado.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaDominioException("La novedad debe tener una descripción.");
        }
        if (gravedad == null) {
            throw new ReglaDominioException("La novedad debe indicar su gravedad.");
        }
        if (fechaReporte == null) {
            throw new ReglaDominioException("La novedad debe indicar la fecha de reporte.");
        }
        this.id = id;
        this.apartamento = apartamento;
        this.descripcion = descripcion;
        this.gravedad = gravedad;
        this.fechaReporte = fechaReporte;
        this.atendida = false;
    }

    public void marcarAtendida() {
        this.atendida = true;
    }

    public IdNovedad getId() {
        return id;
    }

    public IdentificacionApartamento getApartamento() {
        return apartamento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public GravedadNovedad getGravedad() {
        return gravedad;
    }

    public LocalDate getFechaReporte() {
        return fechaReporte;
    }

    public boolean isAtendida() {
        return atendida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Novedad novedad)) return false;
        return Objects.equals(id, novedad.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
