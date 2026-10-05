package co.edu.uniquindio.sga.infraestructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.util.HashMap;
import java.util.Optional;

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
