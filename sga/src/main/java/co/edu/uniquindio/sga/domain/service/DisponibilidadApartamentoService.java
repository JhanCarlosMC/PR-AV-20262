package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ApartamentoNoDisponibleException;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * RN-01 (sin solapamiento con reservas activas, sin importar el canal), RN-07 (sin bloqueo
 * vigente), RN-12 (las reservas no activas no cuentan) y RN-20 (tiempo de preparación entre
 * la salida de un grupo y la entrada del siguiente).
 */
public class DisponibilidadApartamentoService {

    private final ReservaRepository reservaRepository;
    private final BloqueoRepository bloqueoRepository;

    public DisponibilidadApartamentoService(ReservaRepository reservaRepository, BloqueoRepository bloqueoRepository) {
        this.reservaRepository = reservaRepository;
        this.bloqueoRepository = bloqueoRepository;
    }

    public boolean estaDisponible(IdentificacionApartamento apartamento, Estancia estancia, TiempoPreparacion preparacion) {
        Periodo periodo = new Periodo(estancia.fechaEntrada(), estancia.fechaSalida());

        List<Reserva> activas = reservaRepository.buscarActivasPorApartamento(apartamento, periodo);
        for (Reserva otra : activas) {
            // RN-12: una reserva que dejó de estar activa no ocupa noches
            if (!otra.estaActiva()) {
                continue;
            }
            // RN-01: sin importar el canal de origen
            if (estancia.seSolapaCon(otra.getEstancia())) {
                return false;
            }
            // RN-20
            if (violaTiempoPreparacion(estancia, otra.getEstancia(), preparacion)) {
                return false;
            }
        }

        // RN-07
        List<Bloqueo> bloqueos = bloqueoRepository.buscarVigentesPorApartamento(apartamento, periodo);
        for (Bloqueo bloqueo : bloqueos) {
            if (bloqueo.afecta(estancia)) {
                return false;
            }
        }

        return true;
    }

    public void verificarDisponibilidad(IdentificacionApartamento apartamento, Estancia estancia, TiempoPreparacion preparacion) {
        if (!estaDisponible(apartamento, estancia, preparacion)) {
            throw new ApartamentoNoDisponibleException(
                    "El apartamento no está disponible para la estancia solicitada.");
        }
    }

    // RN-20: entre la salida de un grupo y la entrada del siguiente debe respetarse
    // el tiempo de preparación configurado
    private boolean violaTiempoPreparacion(Estancia nueva, Estancia otra, TiempoPreparacion preparacion) {
        long diasPreparacion = Math.ceilDiv(preparacion.horas(), 24);
        if (diasPreparacion == 0) {
            return false;
        }
        if (!nueva.fechaEntrada().isBefore(otra.fechaSalida())) {
            long brecha = ChronoUnit.DAYS.between(otra.fechaSalida(), nueva.fechaEntrada());
            if (brecha < diasPreparacion) {
                return true;
            }
        }
        if (!otra.fechaEntrada().isBefore(nueva.fechaSalida())) {
            long brecha = ChronoUnit.DAYS.between(nueva.fechaSalida(), otra.fechaEntrada());
            if (brecha < diasPreparacion) {
                return true;
            }
        }
        return false;
    }
}
