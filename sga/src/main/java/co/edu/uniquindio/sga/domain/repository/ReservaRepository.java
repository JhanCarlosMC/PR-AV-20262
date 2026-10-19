package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Puerto del dominio: repositorio (Guías 05 y 06) */
@Repository
public interface ReservaRepository {

    List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento, Periodo periodo);
    Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo);
    List<Reserva> buscarPorEstado(EstadoReserva estado);

    void guardar(Reserva reserva);
}
