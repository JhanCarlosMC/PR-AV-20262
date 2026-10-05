package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.FolioNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.PoliticaCancelacionNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.RetencionService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;

import java.time.LocalDateTime;

public class CancelarReservaUseCase {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final RetencionService retencionService;

    public CancelarReservaUseCase(ReservaRepository reservaRepository,
                                  FolioRepository folioRepository,
                                  PoliticaCancelacionRepository politicaRepository,
                                  RetencionService retencionService) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.politicaRepository = politicaRepository;
        this.retencionService = retencionService;
    }

    public Reserva ejecutar(CodigoReserva codigo, String motivo, LocalDateTime momentoActual) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigo));

        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new FolioNoEncontradoException(codigo));

        // RN-13: la política congelada en la reserva, no la vigente hoy
        PoliticaCancelacion politica = politicaRepository.obtenerPorVersion(reserva.getPoliticaCongelada())
                .orElseThrow(() -> new PoliticaCancelacionNoEncontradaException(
                        "versión " + reserva.getPoliticaCongelada().numero()));

        Dinero retencion = retencionService.porCancelacion(reserva, politica, momentoActual);

        // RN-08, RN-12
        reserva.cancelar(motivo, momentoActual.toLocalDate());
        folio.liquidarTerminacion(reserva.getValorCongelado(), retencion, momentoActual.toLocalDate(),
                "cancelación de la reserva " + codigo.valor());

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        return reserva;
    }
}
