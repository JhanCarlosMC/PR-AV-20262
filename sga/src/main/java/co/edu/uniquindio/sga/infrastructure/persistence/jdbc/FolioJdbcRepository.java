package co.edu.uniquindio.sga.infrastructure.persistence.jdbc;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Pago;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.valueobject.Cargo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoFolio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.ConceptoCargo;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.IdPago;
import co.edu.uniquindio.sga.domain.valueobject.MedioPago;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class FolioJdbcRepository implements FolioRepository {

    private final JdbcClient jdbc;

    @Override
    public Optional<Folio> obtenerPorReserva(CodigoReserva reserva) {
        return jdbc.sql("""
                SELECT codigo, cerrado, autorizacion_cierre FROM sga_folio WHERE reserva = ? FOR UPDATE
                """).param(reserva.valor()).query((fila, numero) -> new DatosFolio(fila.getString("codigo"),
                fila.getBoolean("cerrado"), fila.getString("autorizacion_cierre")))
                .optional().map(datos -> reconstruir(reserva, datos));
    }

    @Override
    public void guardar(Folio folio) {
        String reserva = folio.getReserva().valor();
        jdbc.sql("""
                MERGE INTO sga_folio (reserva, codigo, cerrado, autorizacion_cierre)
                KEY (reserva) VALUES (?, ?, ?, ?)
                """).params(reserva, folio.getCodigo().valor(), folio.isCerrado(), folio.getAutorizacionCierre()).update();

        int cargosGuardados = jdbc.sql("SELECT COUNT(*) FROM sga_cargo WHERE reserva = ?")
                .param(reserva).query(Integer.class).single();
        for (int posicion = cargosGuardados; posicion < folio.getCargos().size(); posicion++) {
            Cargo cargo = folio.getCargos().get(posicion);
            jdbc.sql("""
                    INSERT INTO sga_cargo (reserva, posicion, concepto, valor, fecha, descripcion, inverso)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """).params(reserva, posicion, cargo.concepto().name(), cargo.valor().valor(), cargo.fecha(),
                    cargo.descripcion(), cargo.esInverso()).update();
        }

        int pagosGuardados = jdbc.sql("SELECT COUNT(*) FROM sga_pago WHERE reserva = ?")
                .param(reserva).query(Integer.class).single();
        for (int posicion = pagosGuardados; posicion < folio.getPagos().size(); posicion++) {
            Pago pago = folio.getPagos().get(posicion);
            jdbc.sql("""
                    INSERT INTO sga_pago (reserva, posicion, codigo, monto, medio, fecha) VALUES (?, ?, ?, ?, ?, ?)
                    """).params(reserva, posicion, pago.getId().valor(), pago.getMonto().valor(), pago.getMedio().name(),
                    pago.getFecha()).update();
        }
    }

    private Folio reconstruir(CodigoReserva reserva, DatosFolio datos) {
        var folio = new Folio(new CodigoFolio(datos.codigo()), reserva);
        var cargos = jdbc.sql("SELECT * FROM sga_cargo WHERE reserva = ? ORDER BY posicion")
                .param(reserva.valor()).query((fila, numero) -> new Cargo(
                        ConceptoCargo.valueOf(fila.getString("concepto")), new Dinero(fila.getBigDecimal("valor")),
                        fila.getObject("fecha", LocalDate.class), fila.getString("descripcion"),
                        fila.getBoolean("inverso"))).list();
        for (Cargo cargo : cargos) {
            if (cargo.esInverso()) folio.registrarMovimientoInverso(cargo);
            else folio.registrarCargo(cargo);
        }

        jdbc.sql("SELECT * FROM sga_pago WHERE reserva = ? ORDER BY posicion")
                .param(reserva.valor()).query((fila, numero) -> new Pago(new IdPago(fila.getString("codigo")),
                        new Dinero(fila.getBigDecimal("monto")), MedioPago.valueOf(fila.getString("medio")),
                        fila.getObject("fecha", LocalDate.class))).list().forEach(folio::registrarPago);

        if (datos.cerrado()) folio.cerrar(datos.autorizacion());
        return folio;
    }

    private record DatosFolio(String codigo, boolean cerrado, String autorizacion) {}
}
