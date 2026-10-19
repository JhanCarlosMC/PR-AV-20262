package co.edu.uniquindio.sga.infrastructure.config;

import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.DisponibilidadApartamentoService;
import co.edu.uniquindio.sga.domain.service.LiquidacionEstanciaService;
import co.edu.uniquindio.sga.domain.service.OperacionEstanciaService;
import co.edu.uniquindio.sga.domain.service.RetencionService;
import co.edu.uniquindio.sga.domain.valueobject.HoraLimiteNoShow;
import co.edu.uniquindio.sga.domain.valueobject.HorarioAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

/** Servicios de dominio y parámetros del alojamiento registrados como beans (Guía 09, Parte 3) */
@Configuration
public class DomainServiceConfig {

    /** Parámetros de la Ficha del Alojamiento (Anexo A): se leen de application.properties, no se queman en el código */
    @Bean
    public UmbralEdadFacturable umbralEdadFacturable(
            @Value("${sga.alojamiento.umbral-edad-facturable}") int anios) {
        return new UmbralEdadFacturable(anios);
    }

    @Bean
    public HorarioAlojamiento horarioAlojamiento(
            @Value("${sga.alojamiento.hora-entrada}") String horaEntrada,
            @Value("${sga.alojamiento.hora-salida}") String horaSalida) {
        return new HorarioAlojamiento(LocalTime.parse(horaEntrada), LocalTime.parse(horaSalida));
    }

    @Bean
    public TiempoPreparacion tiempoPreparacion(
            @Value("${sga.alojamiento.tiempo-preparacion-horas}") int horas) {
        return new TiempoPreparacion(horas);
    }

    @Bean
    public HoraLimiteNoShow horaLimiteNoShow(
            @Value("${sga.alojamiento.hora-limite-no-show}") String hora) {
        return new HoraLimiteNoShow(LocalTime.parse(hora));
    }

    /** Servicios de dominio: siguen siendo Java puro, Spring los crea aquí sin que el dominio lo sepa */
    @Bean
    public DisponibilidadApartamentoService disponibilidadApartamentoService(
            ReservaRepository reservaRepository, BloqueoRepository bloqueoRepository) {
        return new DisponibilidadApartamentoService(reservaRepository, bloqueoRepository);
    }

    @Bean
    public LiquidacionEstanciaService liquidacionEstanciaService(UmbralEdadFacturable umbral) {
        return new LiquidacionEstanciaService(umbral);
    }

    @Bean
    public OperacionEstanciaService operacionEstanciaService() {
        return new OperacionEstanciaService();
    }

    @Bean
    public RetencionService retencionService(HorarioAlojamiento horario) {
        return new RetencionService(horario);
    }
}
