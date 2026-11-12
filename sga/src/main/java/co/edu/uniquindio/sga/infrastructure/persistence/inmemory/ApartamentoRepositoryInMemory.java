package co.edu.uniquindio.sga.infrastructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/** Adaptador en memoria del puerto (Guía 06) */
public class ApartamentoRepositoryInMemory implements ApartamentoRepository {

    private final Map<IdentificacionApartamento, Apartamento> apartamentos = new ConcurrentHashMap<>();

    @Override
    public Optional<Apartamento> obtenerPorIdentificacion(IdentificacionApartamento identificacion) {
        return Optional.ofNullable(apartamentos.get(identificacion));
    }

    @Override
    public List<Apartamento> buscarTodos() {
        return List.copyOf(apartamentos.values());
    }

    @Override
    public void guardar(Apartamento apartamento) {
        apartamentos.put(apartamento.getIdentificacion(), apartamento);
    }
}
