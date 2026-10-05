package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.PoliticaCancelacionNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Ocupante;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.GeneradorCodigoReserva;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.repository.TemporadaRepository;
import co.edu.uniquindio.sga.domain.service.DisponibilidadApartamentoService;
import co.edu.uniquindio.sga.domain.service.LiquidacionEstanciaService;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.HoraEstimadaLlegada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;

import java.time.LocalDate;
import java.util.List;

public class CrearReservaUseCase {

    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final FolioRepository folioRepository;
    private final TemporadaRepository temporadaRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final GeneradorCodigoReserva generadorCodigo;
    private final DisponibilidadApartamentoService disponibilidadService;
    private final LiquidacionEstanciaService liquidacionService;
    // L-13: configuración del alojamiento, nunca una constante
    private final TiempoPreparacion tiempoPreparacion;

    public CrearReservaUseCase(ReservaRepository reservaRepository,
                               ApartamentoRepository apartamentoRepository,
                               FolioRepository folioRepository,
                               TemporadaRepository temporadaRepository,
                               PoliticaCancelacionRepository politicaRepository,
                               GeneradorCodigoReserva generadorCodigo,
                               DisponibilidadApartamentoService disponibilidadService,
                               LiquidacionEstanciaService liquidacionService,
                               TiempoPreparacion tiempoPreparacion) {
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.folioRepository = folioRepository;
        this.temporadaRepository = temporadaRepository;
        this.politicaRepository = politicaRepository;
        this.generadorCodigo = generadorCodigo;
        this.disponibilidadService = disponibilidadService;
        this.liquidacionService = liquidacionService;
        this.tiempoPreparacion = tiempoPreparacion;
    }

    /** La hora estimada de llegada es opcional al crear; es obligatoria para confirmar (RN-09). */
    public Reserva ejecutar(IdentificacionApartamento apartamentoId, Estancia estancia,
                            HoraEstimadaLlegada horaEstimadaLlegada,
                            Ocupante titular, List<Ocupante> ocupantes,
                            CanalOrigen canalOrigen, LocalDate fechaActual) {

        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(apartamentoId)
                .orElseThrow(() -> new ApartamentoNoEncontradoException(apartamentoId));

        // 7.5 condición 3
        apartamento.verificarQueAceptaReservas();

        // RN-01, RN-07, RN-20
        disponibilidadService.verificarDisponibilidad(apartamentoId, estancia, tiempoPreparacion);

        // RN-05, RN-06: el caso de uso trae las temporadas; el servicio de dominio liquida
        Dinero valor = liquidacionService
                .liquidar(apartamento, estancia, ocupantes, temporadaRepository.buscarTodas())
                .total();

        PoliticaCancelacion politicaVigente = politicaRepository.obtenerVigenteEn(fechaActual)
                .orElseThrow(() -> new PoliticaCancelacionNoEncontradaException("vigente en " + fechaActual));

        CodigoReserva codigo = generadorCodigo.generar();

        // RN-02, RN-04 y RN-22 se verifican dentro de Reserva.crear(...)
        Reserva reserva = Reserva.crear(codigo, apartamentoId, apartamento.getCapacidad(), estancia,
                titular, ocupantes, canalOrigen, valor, politicaVigente.getVersion(), fechaActual);

        if (horaEstimadaLlegada != null) {
            reserva.registrarHoraEstimadaLlegada(horaEstimadaLlegada);
        }

        reservaRepository.guardar(reserva);

        // F-08: el folio se abre al crear la reserva
        folioRepository.guardar(Folio.abrir(codigo, valor, fechaActual));

        return reserva;
    }
}
