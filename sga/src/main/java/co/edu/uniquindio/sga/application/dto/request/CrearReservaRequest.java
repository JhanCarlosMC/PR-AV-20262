package co.edu.uniquindio.sga.application.dto.request;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CrearReservaRequest(
        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotNull(message = "La fecha de entrada es obligatoria")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        // enum cerrado del dominio: @NotNull, no @NotBlank (@NotBlank solo aplica a textos).
        // Un valor desconocido lo rechaza Jackson con 400 (Guía 07, 5.5)
        @NotNull(message = "El canal de origen es obligatorio")
        CanalOrigen canalOrigen,

        // Opcional al crear; obligatoria para confirmar (RN-09)
        LocalTime horaEstimadaLlegada,

        @NotNull(message = "La reserva debe tener titular")
        @Valid
        OcupanteRequest titular,

        // @NotEmpty y no @NotNull: una lista vacía es un mensaje mal escrito (400)
        @NotEmpty(message = "La reserva debe tener al menos un ocupante")
        @Valid
        List<OcupanteRequest> ocupantes
) {
}
