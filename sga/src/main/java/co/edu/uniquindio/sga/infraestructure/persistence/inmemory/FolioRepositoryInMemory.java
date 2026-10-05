package co.edu.uniquindio.sga.infraestructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.util.HashMap;
import java.util.Optional;

public class FolioRepositoryInMemory implements FolioRepository {

    private final HashMap<CodigoReserva, Folio> folios = new HashMap<>();

    @Override
    public Optional<Folio> obtenerPorReserva(CodigoReserva reserva) {
        return Optional.ofNullable(folios.get(reserva));
    }

    @Override
    public void guardar(Folio folio) {
        folios.put(folio.getReserva(), folio);
    }
}
