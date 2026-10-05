package co.edu.uniquindio.sga.infraestructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class PoliticaCancelacionRepositoryInMemory implements PoliticaCancelacionRepository {

    private final HashMap<VersionPolitica, PoliticaCancelacion> politicas = new HashMap<>();

    @Override
    public Optional<PoliticaCancelacion> obtenerVigenteEn(LocalDate fecha) {
        // La vigente es la versión más reciente que ya empezó a regir en esa fecha
        return politicas.values()
                .stream()
                .filter(politica -> !politica.getVersion().vigenteDesde().isAfter(fecha))
                .max(Comparator.comparing(politica -> politica.getVersion().vigenteDesde()));
    }

    @Override
    public Optional<PoliticaCancelacion> obtenerPorVersion(VersionPolitica version) {
        return Optional.ofNullable(politicas.get(version));
    }

    public void guardar(PoliticaCancelacion politica) {
        politicas.put(politica.getVersion(), politica);
    }
}
