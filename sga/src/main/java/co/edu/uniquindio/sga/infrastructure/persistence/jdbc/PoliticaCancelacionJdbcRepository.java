package co.edu.uniquindio.sga.infrastructure.persistence.jdbc;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.TramoRetencion;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PoliticaCancelacionJdbcRepository implements PoliticaCancelacionRepository {

    private static final RowMapper<DatosPolitica> MAPEAR_POLITICA = (fila, numero) -> new DatosPolitica(
            new VersionPolitica(fila.getInt("numero"), fila.getObject("vigente_desde", LocalDate.class)),
            new Porcentaje(fila.getBigDecimal("no_show")));

    private final JdbcClient jdbc;

    @Override
    public Optional<PoliticaCancelacion> obtenerVigenteEn(LocalDate fecha) {
        return jdbc.sql("""
                SELECT numero, vigente_desde, no_show FROM sga_politica_cancelacion
                WHERE vigente_desde <= ? ORDER BY vigente_desde DESC, numero DESC FETCH FIRST 1 ROW ONLY
                """).param(fecha).query(MAPEAR_POLITICA).optional().map(this::cargarTramos);
    }

    @Override
    public Optional<PoliticaCancelacion> obtenerPorVersion(VersionPolitica version) {
        return jdbc.sql("""
                SELECT numero, vigente_desde, no_show FROM sga_politica_cancelacion
                WHERE numero = ? AND vigente_desde = ?
                """).params(version.numero(), version.vigenteDesde())
                .query(MAPEAR_POLITICA).optional().map(this::cargarTramos);
    }

    private PoliticaCancelacion cargarTramos(DatosPolitica datos) {
        var tramos = jdbc.sql("""
                SELECT horas, retencion FROM sga_tramo_retencion
                WHERE numero = ? AND vigente_desde = ? ORDER BY horas
                """).params(datos.version().numero(), datos.version().vigenteDesde())
                .query((fila, numero) -> new TramoRetencion(fila.getInt("horas"), new Porcentaje(fila.getBigDecimal("retencion"))))
                .list();
        return new PoliticaCancelacion(datos.version(), tramos, datos.noShow());
    }

    private record DatosPolitica(VersionPolitica version, Porcentaje noShow) {}
}
