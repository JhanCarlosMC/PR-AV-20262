package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservaDetalleResponse(
        String codigo,                      // "RES-2026-00001", no el objeto CodigoReserva
        EstadoReserva estado,
        CanalOrigen canalOrigen,
        String identificacionApartamento,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        int noches,
        LocalTime horaEstimadaLlegada,      // null hasta que se registre (RN-09)
        OcupanteResponse titular,
        List<OcupanteResponse> ocupantes,
        int totalOcupantes,
        long valorTotal,                    // pesos, sin decimales (3.4)
        String moneda,                      // "COP"
        String versionPoliticaCancelacion,  // la congelada en la reserva (RN-22)
        LocalDate fechaCreacion,
        String motivoCancelacion            // solo si la reserva fue cancelada
    ) {
}
