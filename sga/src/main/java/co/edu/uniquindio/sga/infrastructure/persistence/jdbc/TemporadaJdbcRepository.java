package co.edu.uniquindio.sga.infrastructure.persistence.jdbc;

import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.repository.TemporadaRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TemporadaJdbcRepository implements TemporadaRepository {

    private final JdbcClient jdbc;

    @Override
    public List<Temporada> buscarTodas() {
        return jdbc.sql("SELECT codigo, nombre, desde, hasta, es_base FROM sga_temporada ORDER BY desde DESC, codigo")
                .query((fila, numero) -> new Temporada(
                        new IdTemporada(fila.getString("codigo")), fila.getString("nombre"),
                        new Periodo(fila.getObject("desde", LocalDate.class), fila.getObject("hasta", LocalDate.class)),
                        fila.getBoolean("es_base")))
                .list();
    }
}
