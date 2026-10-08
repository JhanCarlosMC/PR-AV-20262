package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CrearReservaRequest(
        @NotBlank(message = "La identificacion del apartamento es obligatoria")
        String identificacionApartamento,

        @NotNull(message = "La fecha de entrega es obligatoria")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        @NotBlank(message = "El canal de origen es obligatorio")
        String canalOrigen,

        //No es obligatorio
        LocalTime horaEstimadaLlegada,

        @NotNull(message = "La reserva debe tener titular")
        @Valid
        OcupanteRequest titular,

        @NotNull(message = "La reserva debe tener al menos un ocupante")
        @Valid
        List<OcupanteRequest> ocupantes
) {
}
