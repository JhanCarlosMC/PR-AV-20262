package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

/** Caso de uso (Guía 06) */
public class ConfirmarReservaUseCase {

    private final ReservaRepository reservaRepository;

    public ConfirmarReservaUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // Devuelve la reserva para que la API responda con su detalle actualizado (Guía 07)
    public Reserva ejecutar(CodigoReserva codigo){
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigo));

        reserva.confirmar();
        reservaRepository.guardar(reserva);
        return reserva;
    }

}
