package co.edu.uniquindio.sga.infraestructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ReservaRepositoryInMemory implements ReservaRepository {

    private final HashMap<CodigoReserva, Reserva> reservas = new HashMap<>();

    @Override
    public List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento, Periodo periodo) {
        return reservas.values()
                .stream()
                .filter(reserva -> reserva.getApartamento().equals(apartamento))
                .filter(reserva -> reserva.getEstado().esActiva())
                .toList();
    }

    @Override
    public Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo) {
        return Optional.ofNullable(reservas.get(codigo));
    }

    @Override
    public void guardar(Reserva reserva) {
        reservas.put(reserva.getCodigo(), reserva);
    }
}
