package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.MotivoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Raíz del agregado Bloqueo.
 */
public class Bloqueo {

    private final IdBloqueo id;
    private final IdentificacionApartamento apartamento;
    private Periodo periodo;
    private final MotivoBloqueo motivo;
    private String observacion;
    private boolean vigente;

    public Bloqueo(IdBloqueo id, IdentificacionApartamento apartamento, Periodo periodo,
                    MotivoBloqueo motivo, String observacion) {
        if (id == null) {
            throw new ReglaDominioException("El bloqueo debe tener un identificador.");
        }
        if (apartamento == null) {
            throw new ReglaDominioException("El bloqueo debe indicar el apartamento afectado.");
        }
        if (periodo == null) {
            throw new ReglaDominioException("El bloqueo debe indicar el periodo que cubre.");
        }
        if (motivo == null) {
            throw new ReglaDominioException("El bloqueo debe indicar su motivo.");
        }
        this.id = id;
        this.apartamento = apartamento;
        this.periodo = periodo;
        this.motivo = motivo;
        this.observacion = observacion;
        this.vigente = true;
    }

    /** RN-07: un apartamento con bloqueo vigente sobre una noche no está disponible para esa noche. */
    public boolean cubre(LocalDate noche) {
        return vigente && periodo.contiene(noche);
    }

    /** RN-07: una estancia se ve afectada si alguna de sus noches está cubierta por el bloqueo. */
    public boolean afecta(Estancia estancia) {
        return estancia.nochesOcupadas().stream().anyMatch(this::cubre);
    }

    public void levantar() {
        this.vigente = false;
    }

    public IdBloqueo getId() {
        return id;
    }

    public IdentificacionApartamento getApartamento() {
        return apartamento;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public MotivoBloqueo getMotivo() {
        return motivo;
    }

    public String getObservacion() {
        return observacion;
    }

    public boolean isVigente() {
        return vigente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bloqueo bloqueo)) return false;
        return Objects.equals(id, bloqueo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
