package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Raíz del agregado Temporada.
 *
 * <p>F-05: existe una temporada base obligatoria que cubre el resto del año.
 * L-08: cuántas temporadas existen, sus nombres y sus fechas los define el equipo.
 */
public class Temporada {

    private final IdTemporada id;
    private String nombre;
    private Periodo periodo;
    private final boolean esBase;

    public Temporada(IdTemporada id, String nombre, Periodo periodo, boolean esBase) {
        if (id == null) {
            throw new ReglaDominioException("La temporada debe tener un identificador.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("La temporada debe tener un nombre.");
        }
        if (periodo == null) {
            throw new ReglaDominioException("La temporada debe indicar el periodo que cubre.");
        }
        this.id = id;
        this.nombre = nombre;
        this.periodo = periodo;
        this.esBase = esBase;
    }

    /** RN-05: resuelve la temporada vigente en una noche, para tomar su tarifa. */
    public boolean cubre(LocalDate noche) {
        return periodo.contiene(noche);
    }

    public IdTemporada getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public boolean isEsBase() {
        return esBase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Temporada temporada)) return false;
        return Objects.equals(id, temporada.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
