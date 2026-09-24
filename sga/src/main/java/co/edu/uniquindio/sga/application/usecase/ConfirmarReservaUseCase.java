package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradadException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

public class ConfirmarReservaUseCase {

    private final ReservaRepository reservaRepository;

    public ConfirmarReservaUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public void ejecutar(CodigoReserva codigo){
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradadException(codigo));

        reserva.confirmar();
        reservaRepository.guardar(reserva);
    }

}
