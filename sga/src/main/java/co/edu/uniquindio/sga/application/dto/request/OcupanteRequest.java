package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record OcupanteRequest(
        // Sin @Size: el proyecto no define una longitud para el documento,
        // y un límite inventado rechazaría documentos válidos como TI1030998877
        @NotBlank(message = "El documento del ocupante es obligatorio")
        String documento,

        @NotBlank(message = "El nombre del ocupante es obligatorio")
        String nombre,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento no puede ser futura")
        LocalDate fechaNacimiento
) {
}
