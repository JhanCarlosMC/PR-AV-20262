package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento, Periodo periodo);
    Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo);

    void guardar(Reserva reserva);
}
