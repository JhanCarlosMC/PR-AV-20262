package co.edu.uniquindio.sga.infrastructure.persistence.jdbc;

import co.edu.uniquindio.sga.domain.repository.GeneradorCodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class GeneradorCodigoReservaJdbc implements GeneradorCodigoReserva {

    private final JdbcClient jdbc;

    @Override
    public CodigoReserva generar() {
        int anio = Year.now().getValue();
        while (true) {
            long consecutivo = jdbc.sql("SELECT NEXT VALUE FOR sga_codigo_reserva").query(Long.class).single();
            String codigo = "RES-%d-%05d".formatted(anio, consecutivo);
            if (jdbc.sql("SELECT 1 FROM reserva WHERE codigo = ?").param(codigo).query(Integer.class).optional().isEmpty()) {
                return new CodigoReserva(codigo);
            }
        }
    }
}
