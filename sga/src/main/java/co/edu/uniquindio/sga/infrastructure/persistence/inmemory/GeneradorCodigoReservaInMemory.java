package co.edu.uniquindio.sga.infrastructure.persistence.inmemory;

import co.edu.uniquindio.sga.domain.repository.GeneradorCodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

// Formato del negocio: RES-YYYY-NNNNN (ejemplo: RES-2026-00042)
/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Component
public class GeneradorCodigoReservaInMemory implements GeneradorCodigoReserva {

    private final AtomicInteger consecutivo = new AtomicInteger();

    @Override
    public CodigoReserva generar() {
        return new CodigoReserva("RES-%d-%05d".formatted(Year.now().getValue(), consecutivo.incrementAndGet()));
    }
}
