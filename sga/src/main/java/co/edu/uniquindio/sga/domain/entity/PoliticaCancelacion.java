package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.TramoRetencion;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;

import java.util.List;
import java.util.Objects;

/**
 * Raíz del agregado PoliticaCancelacion.
 *
 * <p>F-07: existe una única política por alojamiento, versionada en el tiempo.
 * L-10: el equipo define los tramos de antelación, con un mínimo de dos.
 */
public class PoliticaCancelacion {

    private final VersionPolitica version;
    private final List<TramoRetencion> tramos;
    private final Porcentaje retencionNoShow;

    public PoliticaCancelacion(VersionPolitica version, List<TramoRetencion> tramos, Porcentaje retencionNoShow) {
        if (version == null) {
            throw new ReglaDominioException("La política de cancelación debe tener una versión.");
        }
        if (tramos == null || tramos.size() < 2) {
            throw new ReglaDominioException("La política de cancelación debe tener al menos dos tramos de antelación.");
        }
        if (retencionNoShow == null) {
            throw new ReglaDominioException("La política de cancelación debe indicar la retención por no-show.");
        }
        this.version = version;
        this.tramos = List.copyOf(tramos);
        this.retencionNoShow = retencionNoShow;
    }

    /** RN-13: escoge el tramo aplicable según la antelación con la que ocurre la cancelación. */
    public Porcentaje retencionPara(long horasAntelacion) {
        TramoRetencion tramoAplicable = null;
        for (TramoRetencion tramo : tramos) {
            if (horasAntelacion < tramo.horasAntelacionMinima()) {
                continue;
            }
            if (tramoAplicable == null || tramo.horasAntelacionMinima() > tramoAplicable.horasAntelacionMinima()) {
                tramoAplicable = tramo;
            }
        }
        if (tramoAplicable == null) {
            throw new ReglaDominioException("No existe un tramo de retención aplicable para la antelación indicada.");
        }
        return tramoAplicable.retencion();
    }

    /** RN-13. */
    public Porcentaje retencionPorNoShow() {
        return retencionNoShow;
    }

    public VersionPolitica getVersion() {
        return version;
    }

    public List<TramoRetencion> getTramos() {
        return tramos;
    }

    public Porcentaje getRetencionNoShow() {
        return retencionNoShow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PoliticaCancelacion that)) return false;
        return Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version);
    }
}
