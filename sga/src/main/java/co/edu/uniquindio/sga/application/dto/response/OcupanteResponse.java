package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;

/** Ocupante tal como lo ve quien consume la API. */
public record OcupanteResponse(
        String documento,
        String nombre,
        LocalDate fechaNacimiento
) {
}
