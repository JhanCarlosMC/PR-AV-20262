package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Ocupante;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.service.DisponibilidadApartamentoService;
import co.edu.uniquindio.sga.domain.service.LiquidacionEstanciaService;
import co.edu.uniquindio.sga.domain.service.OperacionEstanciaService;
import co.edu.uniquindio.sga.domain.service.RetencionService;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.DocumentoIdentidad;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.HoraEstimadaLlegada;
import co.edu.uniquindio.sga.domain.valueobject.HoraLimiteNoShow;
import co.edu.uniquindio.sga.domain.valueobject.HorarioAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;
import co.edu.uniquindio.sga.domain.valueobject.TramoRetencion;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;
import co.edu.uniquindio.sga.infraestructure.persistence.inmemory.ApartamentoRepositoryInMemory;
import co.edu.uniquindio.sga.infraestructure.persistence.inmemory.BloqueoRepositoryInMemory;
import co.edu.uniquindio.sga.infraestructure.persistence.inmemory.FolioRepositoryInMemory;
import co.edu.uniquindio.sga.infraestructure.persistence.inmemory.GeneradorCodigoReservaInMemory;
import co.edu.uniquindio.sga.infraestructure.persistence.inmemory.PoliticaCancelacionRepositoryInMemory;
import co.edu.uniquindio.sga.infraestructure.persistence.inmemory.ReservaRepositoryInMemory;
import co.edu.uniquindio.sga.infraestructure.persistence.inmemory.TemporadaRepositoryInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CicloDeVidaReservaUseCaseTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 1);
    private static final IdentificacionApartamento APT_301 = new IdentificacionApartamento("APT-301");
    private static final Estancia ESTANCIA = new Estancia(LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 12));

    private final Ocupante titular = new Ocupante(new DocumentoIdentidad("1094123456"), "Ana Ramírez", LocalDate.of(1990, 5, 12));
    private final Ocupante acompanante = new Ocupante(new DocumentoIdentidad("1094999888"), "Luis Ramírez", LocalDate.of(1988, 2, 1));
    private final Ocupante nina = new Ocupante(new DocumentoIdentidad("TI1030998877"), "Sofía Ramírez", LocalDate.of(2019, 3, 2));

    private ReservaRepositoryInMemory reservaRepository;
    private ApartamentoRepositoryInMemory apartamentoRepository;
    private FolioRepositoryInMemory folioRepository;
    private Apartamento apartamento;

    private CrearReservaUseCase crear;
    private ConsultarReservaUseCase consultar;
    private ConsultarReservasPorEstadoUseCase consultarPorEstado;
    private ConfirmarReservaUseCase confirmar;
    private CancelarReservaUseCase cancelar;
    private RegistrarLlegadaUseCase registrarLlegada;
    private RegistrarSalidaUseCase registrarSalida;
    private DeclararNoShowUseCase declararNoShow;

    @BeforeEach
    void configurar() {
        reservaRepository = new ReservaRepositoryInMemory();
        apartamentoRepository = new ApartamentoRepositoryInMemory();
        folioRepository = new FolioRepositoryInMemory();
        var temporadaRepository = new TemporadaRepositoryInMemory();
        var politicaRepository = new PoliticaCancelacionRepositoryInMemory();

        IdTemporada base = new IdTemporada("BASE");
        temporadaRepository.guardar(new Temporada(base, "Base",
                new Periodo(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 12, 31)), true));

        apartamento = new Apartamento(APT_301, "Apartamento 301", 2, 4);
        apartamento.definirTarifa(new Tarifa(base, dinero(200_000)));
        apartamentoRepository.guardar(apartamento);

        // Más de 72 horas de antelación: sin retención. Menos: retiene el 50 %. No-show: 100 %.
        politicaRepository.guardar(new PoliticaCancelacion(
                new VersionPolitica(1, LocalDate.of(2026, 1, 1)),
                List.of(new TramoRetencion(0, porcentaje(50)), new TramoRetencion(72, porcentaje(0))),
                porcentaje(100)));

        var disponibilidad = new DisponibilidadApartamentoService(reservaRepository, new BloqueoRepositoryInMemory());
        var retencion = new RetencionService(new HorarioAlojamiento(LocalTime.of(15, 0), LocalTime.of(11, 0)));
        var operacion = new OperacionEstanciaService();

        crear = new CrearReservaUseCase(reservaRepository, apartamentoRepository, folioRepository,
                temporadaRepository, politicaRepository, new GeneradorCodigoReservaInMemory(), disponibilidad,
                new LiquidacionEstanciaService(new UmbralEdadFacturable(12)), new TiempoPreparacion(0));
        consultar = new ConsultarReservaUseCase(reservaRepository);
        consultarPorEstado = new ConsultarReservasPorEstadoUseCase(reservaRepository);
        confirmar = new ConfirmarReservaUseCase(reservaRepository);
        cancelar = new CancelarReservaUseCase(reservaRepository, folioRepository, politicaRepository, retencion);
        registrarLlegada = new RegistrarLlegadaUseCase(reservaRepository, apartamentoRepository, operacion);
        registrarSalida = new RegistrarSalidaUseCase(reservaRepository, apartamentoRepository, folioRepository, operacion);
        declararNoShow = new DeclararNoShowUseCase(reservaRepository, folioRepository, politicaRepository, retencion,
                new HoraLimiteNoShow(LocalTime.of(22, 0)));
    }

    @Test
    void crearReservaCongelaElValorYAbreElFolio() {
        Reserva reserva = crearReserva();

        // 2 noches × 2 ocupantes facturables × 200.000 (la niña no es facturable, RN-06)
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertEquals(dinero(800_000), reserva.getValorCongelado());
        assertEquals(dinero(800_000), folio(reserva).saldo());
        assertEquals(reserva, consultar.ejecutar(reserva.getCodigo()));
    }

    @Test
    void crearReservaSolapadaEsRechazada() {
        crearReserva();
        assertThrows(ReglaDominioException.class, this::crearReserva);
    }

    @Test
    void consultarReservaInexistenteLanzaNoEncontrada() {
        assertThrows(ReservaNoEncontradaException.class,
                () -> consultar.ejecutar(new CodigoReserva("RES-2026-99999")));
    }

    @Test
    void cicloCompletoHastaLaSalida() {
        Reserva reserva = crearReserva();
        CodigoReserva codigo = reserva.getCodigo();

        confirmar.ejecutar(codigo);
        assertEquals(List.of(reserva), consultarPorEstado.ejecutar(EstadoReserva.CONFIRMADA));

        registrarLlegada.ejecutar(codigo, ESTANCIA.fechaEntrada());
        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());
        assertEquals(EstadoOperativo.OCUPADO, apartamento.getEstadoOperativo());

        // RN-17: saldo pendiente sin autorización, nada cambia
        assertThrows(ReglaDominioException.class,
                () -> registrarSalida.ejecutar(codigo, null, ESTANCIA.fechaSalida()));
        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());

        registrarSalida.ejecutar(codigo, "Autorizado por administración", ESTANCIA.fechaSalida());
        assertEquals(EstadoReserva.FINALIZADA, reserva.getEstado());
        assertEquals(EstadoOperativo.PENDIENTE_PREPARACION, apartamento.getEstadoOperativo());
        assertTrue(folio(reserva).isCerrado());
    }

    @Test
    void cancelarConAntelacionNoRetieneNada() {
        Reserva reserva = crearReserva();

        cancelar.ejecutar(reserva.getCodigo(), "El huésped cambió de planes", HOY.atTime(9, 0));

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(folio(reserva).saldo().esCero());
    }

    @Test
    void cancelarSinAntelacionRetieneSegunLaPoliticaCongelada() {
        Reserva reserva = crearReserva();

        cancelar.ejecutar(reserva.getCodigo(), "Imprevisto familiar", LocalDateTime.of(2026, 10, 9, 20, 0));

        assertEquals(dinero(400_000), folio(reserva).saldo());
    }

    @Test
    void noShowSoloDespuesDeLaHoraLimite() {
        Reserva reserva = crearReserva();
        confirmar.ejecutar(reserva.getCodigo());

        assertThrows(ReglaDominioException.class,
                () -> declararNoShow.ejecutar(reserva.getCodigo(), ESTANCIA.fechaEntrada().atTime(18, 0)));

        declararNoShow.ejecutar(reserva.getCodigo(), ESTANCIA.fechaEntrada().atTime(22, 30));
        assertEquals(EstadoReserva.NO_SHOW, reserva.getEstado());
        assertEquals(dinero(800_000), folio(reserva).saldo());
    }

    private Reserva crearReserva() {
        return crear.ejecutar(APT_301, ESTANCIA, new HoraEstimadaLlegada(LocalTime.of(15, 0)),
                titular, List.of(titular, acompanante, nina), CanalOrigen.PORTAL, HOY);
    }

    private Folio folio(Reserva reserva) {
        return folioRepository.obtenerPorReserva(reserva.getCodigo()).orElseThrow();
    }

    private static Dinero dinero(long valor) {
        return new Dinero(BigDecimal.valueOf(valor));
    }

    private static Porcentaje porcentaje(long valor) {
        return new Porcentaje(BigDecimal.valueOf(valor));
    }
}
