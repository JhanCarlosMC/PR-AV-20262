package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;

public record ReservaResumenResponse(
        String codigo,
        EstadoReserva estado,
        String identificacionApartamento,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        String nombreTitular,
        int totalOcupantes,
        long valorTotal                     // pesos, sin decimales (3.4)

) {
}
