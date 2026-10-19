package co.edu.uniquindio.sga.infrastructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;

import java.util.HashMap;
import java.util.List;

/** Adaptador en memoria del puerto (Guía 06) */
public class BloqueoRepositoryInMemory implements BloqueoRepository {

    private final HashMap<IdBloqueo, Bloqueo> bloqueos = new HashMap<>();

    @Override
    public List<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento, Periodo periodo) {
        return bloqueos.values()
                .stream()
                .filter(bloqueo -> bloqueo.getApartamento().equals(apartamento))
                .filter(Bloqueo::isVigente)
                .filter(bloqueo -> bloqueo.getPeriodo().seSolapaCon(periodo))
                .toList();
    }

    public void guardar(Bloqueo bloqueo) {
        bloqueos.put(bloqueo.getId(), bloqueo);
    }
}
