package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso (Guía 06), servicio de Spring con transacción (Guía 09) */
@Service
public class ConfirmarReservaUseCase {

    private final ReservaRepository reservaRepository;

    public ConfirmarReservaUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // Devuelve la reserva para que la API responda con su detalle actualizado (Guía 07)
    @Transactional
    public Reserva ejecutar(CodigoReserva codigo){
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigo));

        reserva.confirmar();
        reservaRepository.guardar(reserva);
        return reserva;
    }

}
