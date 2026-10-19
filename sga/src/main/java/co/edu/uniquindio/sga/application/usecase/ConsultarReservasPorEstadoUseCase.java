package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// La paginación de 10 por página (7.5, 12.4) llega con la Guía 11
/** Caso de uso (Guía 06), servicio de Spring con transacción (Guía 09) */
@Service
public class ConsultarReservasPorEstadoUseCase {

    private final ReservaRepository reservaRepository;

    public ConsultarReservasPorEstadoUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<Reserva> ejecutar(EstadoReserva estado) {
        return reservaRepository.buscarPorEstado(estado);
    }
}
