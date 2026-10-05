package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.FolioNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.OperacionEstanciaService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.time.LocalDate;

public class RegistrarSalidaUseCase {

    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final FolioRepository folioRepository;
    private final OperacionEstanciaService operacionService;

    public RegistrarSalidaUseCase(ReservaRepository reservaRepository,
                                  ApartamentoRepository apartamentoRepository,
                                  FolioRepository folioRepository,
                                  OperacionEstanciaService operacionService) {
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.folioRepository = folioRepository;
        this.operacionService = operacionService;
    }

    /**
     * @param autorizacionCierre opcional. Solo se exige si el saldo del folio no es cero (RN-17);
     *                           el caso de uso no decide si autorizar, solo la transporta.
     */
    public Reserva ejecutar(CodigoReserva codigo, String autorizacionCierre, LocalDate fechaActual) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigo));

        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(reserva.getApartamento())
                .orElseThrow(() -> new ApartamentoNoEncontradoException(reserva.getApartamento()));

        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new FolioNoEncontradoException(codigo));

        operacionService.registrarSalida(reserva, apartamento, folio, autorizacionCierre, fechaActual);

        reservaRepository.guardar(reserva);
        apartamentoRepository.guardar(apartamento);
        folioRepository.guardar(folio);
        return reserva;
    }
}
