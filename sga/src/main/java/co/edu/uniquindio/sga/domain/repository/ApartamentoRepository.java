package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.util.Optional;

public interface ApartamentoRepository {

    Optional<Apartamento> obtenerPorIdentificacion(IdentificacionApartamento identificacion);

    void guardar(Apartamento apartamento);
}
