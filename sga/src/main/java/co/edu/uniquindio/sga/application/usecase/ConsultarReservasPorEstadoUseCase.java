package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.util.List;

// La paginación de 10 por página (7.5, 12.4) llega con la Guía 11
public class ConsultarReservasPorEstadoUseCase {

    private final ReservaRepository reservaRepository;

    public ConsultarReservasPorEstadoUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> ejecutar(EstadoReserva estado) {
        return reservaRepository.buscarPorEstado(estado);
    }
}
