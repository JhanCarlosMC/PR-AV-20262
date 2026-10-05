package co.edu.uniquindio.sga.domain.valueobject;

import java.time.LocalDate;

// RN-05 y RN-06: cada noche se liquida con la tarifa de su temporada por ocupante facturable
public record NocheLiquidada(LocalDate fecha,
                             IdTemporada temporada,
                             Dinero tarifaPorOcupanteFacturable,
                             int ocupantesFacturables,
                             Dinero subtotal) {
}
