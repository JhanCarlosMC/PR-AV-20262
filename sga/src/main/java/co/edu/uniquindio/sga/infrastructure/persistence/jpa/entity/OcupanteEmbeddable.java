package co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;

import java.time.LocalDate;

public record OcupanteEmbeddable(
        @Column(name = "documento", nullable = false, length = 15)
        String documento,
        @Column(name = "nombre_completo", nullable = false, length = 150)
        String nombreCompleto,
        @Column(name = "fecha_nacimiento", nullable = false, length = 15)
        LocalDate fechaNacimiento
) {
}