package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Temporada;

import java.util.List;

/** Puerto del dominio: repositorio (Guías 05 y 06) */
public interface TemporadaRepository {

    List<Temporada> buscarTodas();
}
