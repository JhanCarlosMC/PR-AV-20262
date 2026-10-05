package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.util.Optional;

// 7.7: cada reserva tiene exactamente un folio, por eso se busca por la reserva
public interface FolioRepository {

    Optional<Folio> obtenerPorReserva(CodigoReserva reserva);

    void guardar(Folio folio);
}
