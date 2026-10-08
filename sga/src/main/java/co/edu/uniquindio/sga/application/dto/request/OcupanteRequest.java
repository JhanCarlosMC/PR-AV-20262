package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record OcupanteRequest(
        @NotBlank(message = "El documento del ocupante es obligatorio")
        @Size(min=7, max=11)
        String documento,

        @NotBlank(message = "El nombre del ocupante es obligatorio")
        String nombre,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento no puede ser futura")
        LocalDate fechaNacimiento
) {
}
