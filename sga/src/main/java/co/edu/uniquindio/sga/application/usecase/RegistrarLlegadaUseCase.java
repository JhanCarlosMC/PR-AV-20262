package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.OperacionEstanciaService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso (Guía 06), servicio de Spring con transacción (Guía 09) */
@Service
public class RegistrarLlegadaUseCase {

    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final OperacionEstanciaService operacionService;

    public RegistrarLlegadaUseCase(ReservaRepository reservaRepository,
                                   ApartamentoRepository apartamentoRepository,
                                   OperacionEstanciaService operacionService) {
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.operacionService = operacionService;
    }

    @Transactional
    public Reserva ejecutar(CodigoReserva codigo, LocalDate fechaActual) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigo));

        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(reserva.getApartamento())
                .orElseThrow(() -> new ApartamentoNoEncontradoException(reserva.getApartamento()));

        operacionService.registrarLlegada(reserva, apartamento, fechaActual);

        reservaRepository.guardar(reserva);
        apartamentoRepository.guardar(apartamento);
        return reserva;
    }
}
