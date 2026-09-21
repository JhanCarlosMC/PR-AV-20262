package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;

import java.util.List;

public interface ReservaRepository {

    List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento, Periodo periodo);
}
