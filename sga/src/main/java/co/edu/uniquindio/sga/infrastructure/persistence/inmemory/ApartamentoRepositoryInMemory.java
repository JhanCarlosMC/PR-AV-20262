package co.edu.uniquindio.sga.infrastructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Repository
public class ApartamentoRepositoryInMemory implements ApartamentoRepository {

    private final HashMap<IdentificacionApartamento, Apartamento> apartamentos = new HashMap<>();

    @Override
    public Optional<Apartamento> obtenerPorIdentificacion(IdentificacionApartamento identificacion) {
        return Optional.ofNullable(apartamentos.get(identificacion));
    }

    @Override
    public void guardar(Apartamento apartamento) {
        apartamentos.put(apartamento.getIdentificacion(), apartamento);
    }
}
