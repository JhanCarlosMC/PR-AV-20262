package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;

/** DTO de salida de la API (Guía 07) */
public record OcupanteResponse(
        String documento,
        String nombre,
        LocalDate fechaNacimiento
) {
}
