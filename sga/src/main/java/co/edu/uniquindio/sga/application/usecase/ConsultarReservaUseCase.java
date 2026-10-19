package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso (Guía 06), servicio de Spring con transacción (Guía 09) */
@Service
public class ConsultarReservaUseCase {

    private final ReservaRepository reservaRepository;

    public ConsultarReservaUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public Reserva ejecutar(CodigoReserva codigo) {
        return reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigo));
    }
}
