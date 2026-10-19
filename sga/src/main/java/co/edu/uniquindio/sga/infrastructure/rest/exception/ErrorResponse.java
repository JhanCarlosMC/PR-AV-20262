package co.edu.uniquindio.sga.infrastructure.rest.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Manejo de excepciones (Guía 08, Parte 4)
 *
 * Forma de todos los errores de la API: schema ErrorResponse de docs/api/openapi.yaml (Guía 07).
 * "errores" solo se llena en un 400 de Bean Validation (campo → mensaje).
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        Map<String, String> errores
) {
}
