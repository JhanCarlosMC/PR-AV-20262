package co.edu.uniquindio.sga.infrastructure.persistence.jdbc;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class ApartamentoJdbcRepository implements ApartamentoRepository {

    private static final RowMapper<Apartamento> MAPEAR_APARTAMENTO = (fila, numero) -> Apartamento.reconstruir(
            new IdentificacionApartamento(fila.getString("codigo")), fila.getString("nombre"),
            fila.getInt("dormitorios"), fila.getInt("capacidad"),
            EstadoOperativo.valueOf(fila.getString("estado_operativo")), fila.getBoolean("activo"));

    private final JdbcClient jdbc;

    @Override
    @Transactional(readOnly = true)
    public Optional<Apartamento> obtenerPorIdentificacion(IdentificacionApartamento identificacion) {
        return jdbc.sql("SELECT * FROM sga_apartamento WHERE codigo = ?")
                .param(identificacion.valor()).query(MAPEAR_APARTAMENTO).optional()
                .map(this::cargarTarifas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Apartamento> buscarTodos() {
        var apartamentos = jdbc.sql("SELECT * FROM sga_apartamento").query(MAPEAR_APARTAMENTO).list();
        var tarifas = jdbc.sql("SELECT apartamento, temporada, valor FROM sga_tarifa").query((fila, numero) ->
                new TarifaAsignada(fila.getString("apartamento"), new Tarifa(
                        new IdTemporada(fila.getString("temporada")), new Dinero(fila.getBigDecimal("valor")))))
                .list().stream().collect(java.util.stream.Collectors.groupingBy(TarifaAsignada::apartamento));
        apartamentos.forEach(apartamento -> tarifas.getOrDefault(apartamento.getIdentificacion().valor(), List.of())
                .forEach(asignada -> apartamento.definirTarifa(asignada.tarifa())));
        return apartamentos;
    }

    @Override
    public void guardar(Apartamento apartamento) {
        String codigo = apartamento.getIdentificacion().valor();
        jdbc.sql("""
                MERGE INTO sga_apartamento (codigo, nombre, dormitorios, capacidad, estado_operativo, activo)
                KEY (codigo) VALUES (?, ?, ?, ?, ?, ?)
                """).params(codigo, apartamento.getNombre(), apartamento.getDormitorios(), apartamento.getCapacidad(),
                apartamento.getEstadoOperativo().name(), apartamento.estaActivo()).update();

        for (Tarifa tarifa : apartamento.getTarifas()) {
            jdbc.sql("""
                    MERGE INTO sga_tarifa (apartamento, temporada, valor)
                    KEY (apartamento, temporada) VALUES (?, ?, ?)
                    """).params(codigo, tarifa.temporada().valor(), tarifa.valorPorOcupanteNoche().valor()).update();
        }
    }

    private Apartamento cargarTarifas(Apartamento apartamento) {
        jdbc.sql("SELECT temporada, valor FROM sga_tarifa WHERE apartamento = ?")
                .param(apartamento.getIdentificacion().valor())
                .query((fila, numero) -> new Tarifa(new IdTemporada(fila.getString("temporada")),
                        new Dinero(fila.getBigDecimal("valor"))))
                .list().forEach(apartamento::definirTarifa);
        return apartamento;
    }

    private record TarifaAsignada(String apartamento, Tarifa tarifa) {}
}
