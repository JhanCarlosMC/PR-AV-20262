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
import co.edu.uniquindio.sga.domain.valueobject.HoraLimiteNoShow;

import java.time.LocalDateTime;

// Disparador distinto de la cancelación: no se fusiona con CancelarReservaUseCase (Guía 06, 3.3)
public class DeclararNoShowUseCase {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final RetencionService retencionService;
    // L-15: configuración del alojamiento, nunca una constante
    private final HoraLimiteNoShow horaLimite;

    public DeclararNoShowUseCase(ReservaRepository reservaRepository,
                                 FolioRepository folioRepository,
                                 PoliticaCancelacionRepository politicaRepository,
                                 RetencionService retencionService,
                                 HoraLimiteNoShow horaLimite) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.politicaRepository = politicaRepository;
        this.retencionService = retencionService;
        this.horaLimite = horaLimite;
    }

    public Reserva ejecutar(CodigoReserva codigo, LocalDateTime momentoActual) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigo));

        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new FolioNoEncontradoException(codigo));

        // RN-13: la política congelada en la reserva, no la vigente hoy
        PoliticaCancelacion politica = politicaRepository.obtenerPorVersion(reserva.getPoliticaCongelada())
                .orElseThrow(() -> new PoliticaCancelacionNoEncontradaException(
                        "versión " + reserva.getPoliticaCongelada().numero()));

        Dinero retencion = retencionService.porNoShow(reserva, politica);

        // RN-08, L-15
        reserva.declararNoShow(momentoActual, horaLimite);
        folio.liquidarTerminacion(reserva.getValorCongelado(), retencion, momentoActual.toLocalDate(),
                "no-show de la reserva " + codigo.valor());

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        return reserva;
    }
}
