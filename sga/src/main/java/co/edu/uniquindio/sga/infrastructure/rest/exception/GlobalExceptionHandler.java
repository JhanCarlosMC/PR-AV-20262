package co.edu.uniquindio.sga.infrastructure.rest.exception;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.FolioNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convención de la Guía 07 (4.6):
 * 400 el mensaje está mal escrito · 404 el identificador no existe · 409 el negocio no lo permite.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Manejo de errores de validación (Bean Validation) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "La petición tiene datos inválidos", request, errores);
    }

    /** JSON mal formado, fecha inválida, valor de enum inexistente (en el cuerpo o en la URL) */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleFormatoInvalido(RuntimeException ex, HttpServletRequest request) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "La petición tiene un formato inválido: revise el JSON, las fechas (AAAA-MM-DD) "
                        + "y los valores permitidos", request, null);
    }

    /** Manejo de excepciones de aplicación: el identificador no existe */
    @ExceptionHandler({ReservaNoEncontradaException.class,
            ApartamentoNoEncontradoException.class,
            FolioNoEncontradoException.class})
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RuntimeException ex, HttpServletRequest request) {

        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /** Manejo de excepciones del dominio: toda regla violada (RN-01 … RN-22) */
    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<ErrorResponse> handleReglaDominio(ReglaDominioException ex, HttpServletRequest request) {

        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    /** Excepciones genéricas */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

        /**
         * Los errores propios de Spring MVC (ruta inexistente 404, método no permitido 405…)
         * ya traen su código: se respeta en lugar de convertirlos en 500
         */
        if (ex instanceof org.springframework.web.ErrorResponse errorDeSpring) {
            HttpStatus status = HttpStatus.valueOf(errorDeSpring.getStatusCode().value());
            return buildErrorResponse(status, status.getReasonPhrase(), request, null);
        }

        /** La traza va al log, no al cliente */
        log.error("Error inesperado en {}", request.getRequestURI(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request, null);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, String mensaje, HttpServletRequest request, Map<String, String> errores) {

        ErrorResponse response = new ErrorResponse(Instant.now(), status.value(),
                status.getReasonPhrase(), mensaje, request.getRequestURI(), errores);

        return ResponseEntity.status(status).body(response);
    }
}
