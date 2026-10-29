package co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public record PoliticaEmbeddable(
        @Column(name = "version", nullable = false)
        int numero,
        @Column(name = "fecha_vigencia", nullable = false)
        LocalDate vigenteDesde
) {
}
