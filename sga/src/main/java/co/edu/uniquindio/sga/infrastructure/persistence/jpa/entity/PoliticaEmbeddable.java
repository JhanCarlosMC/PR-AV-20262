package co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record PoliticaEmbeddable(
        @Column(name = "version", nullable = false)
        int numero,
        @Column(name = "fecha_vigencia", nullable = false)
        LocalDate vigenteDesde
) {
}
