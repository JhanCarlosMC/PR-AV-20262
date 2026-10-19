package co.edu.uniquindio.sga.infrastructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.repository.TemporadaRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Repository
public class TemporadaRepositoryInMemory implements TemporadaRepository {

    private final HashMap<IdTemporada, Temporada> temporadas = new HashMap<>();

    @Override
    public List<Temporada> buscarTodas() {
        return List.copyOf(temporadas.values());
    }

    public void guardar(Temporada temporada) {
        temporadas.put(temporada.getId(), temporada);
    }
}
