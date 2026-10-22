package co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public record EstanciaEmbeddable(
        @Column(name = "fecha_entrada", nullable = false)
        LocalDate fechaEntrada,
        @Column(name = "fecha_salida", nullable = false)
        LocalDate fechaSalida
) {
}
