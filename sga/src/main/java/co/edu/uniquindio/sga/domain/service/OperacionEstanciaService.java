package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;

public class OperacionEstanciaService {

    /** RN-08, RN-10 (en Reserva) y RN-11: el apartamento debe estar activo y PREPARADO. */
    public void registrarLlegada(Reserva reserva, Apartamento apartamento, LocalDate fechaActual) {
        verificarCorrespondencia(reserva, apartamento);
        if (!apartamento.puedeRecibirGrupo()) {
            throw new ReglaDominioException(
                    "El apartamento no puede recibir al grupo: debe estar activo y en estado PREPARADO.");
        }
        reserva.registrarLlegada(fechaActual);
        apartamento.marcarOcupado();
    }

    /**
     * RN-08 y RN-17: el folio debe cerrarse para registrar la salida. Si el saldo no es cero,
     * se exige la autorización explícita; quién puede darla lo decide la seguridad (Guía 12),
     * no este servicio ni el caso de uso. El Folio decide si la autorización es necesaria.
     */
    public void registrarSalida(Reserva reserva, Apartamento apartamento, Folio folio,
                                String autorizacionCierre, LocalDate fechaActual) {
        verificarCorrespondencia(reserva, apartamento);
        if (!folio.getReserva().equals(reserva.getCodigo())) {
            throw new ReglaDominioException("El folio no corresponde a la reserva.");
        }
        if (!reserva.getEstado().puedeTransitarA(EstadoReserva.FINALIZADA)) {
            throw new ReglaDominioException(
                    "No es posible registrar la salida de una reserva en estado " + reserva.getEstado() + ".");
        }
        folio.cerrar(autorizacionCierre);
        reserva.registrarSalida(fechaActual);
        apartamento.marcarPendientePreparacion();
    }

    private void verificarCorrespondencia(Reserva reserva, Apartamento apartamento) {
        if (!reserva.getApartamento().equals(apartamento.getIdentificacion())) {
            throw new ReglaDominioException("El apartamento no corresponde al de la reserva.");
        }
    }
}
