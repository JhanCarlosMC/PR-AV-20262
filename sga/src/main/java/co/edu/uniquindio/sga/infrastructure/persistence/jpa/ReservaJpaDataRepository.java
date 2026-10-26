package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaJpaDataRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByEstado(EstadoReserva estado);
    Optional<ReservaEntity> findByCodigo(String codigo);
    //WHERE canal_origen = X AND estado = Y
    List<ReservaEntity> findByCanalOrigenAndEstado(CanalOrigen canalOrigen, EstadoReserva estado);

    //Consulta reserva con fecha de entrada y un estado
    //WHERE fecha_entrada = date AND estado = Y
    List<ReservaEntity> findByEstancia_FechaEntradaAndEstado(LocalDate fechaEntrada, EstadoReserva estado);

    List<ReservaEntity> findByApartamentoIdAndEstadoIn(String apartamentoId, Collection<EstadoReserva> estados);

    long countByEstado(EstadoReserva estado);
    boolean existsByCodigo(String codigo);

    @Query("SELECT r " +
            "FROM ReservaEntity r " +
            "WHERE r.estancia.fechaSalida = ?1 AND r.estado = ?2")
    List<ReservaEntity> buscarPorSalidas(LocalDate fechaSalida, EstadoReserva estado);

    //Buscar las ReservasSolapadasActivas
    @Query("""
        SELECT r
        FROM ReservaEntity r
        WHERE r.apartamentoId = :apartamentoId
                AND r.estancia.fechaEntrada < :fechaSalida
                AND :fechaEntrada <= r.estancia.fechaSalida
                AND r.estado = :estado
        """)
    List<ReservaEntity> buscarSolapadasActivas(@Param("apartamentoId") String apartamentoId,
                                               @Param("fechaSalida") LocalDate fechaSalida,
                                               @Param("fechaEntrada") LocalDate fechaEntrada,
                                               @Param("estado") EstadoReserva estado);


    @Query("SELECT r FROM ReservaEntity r WHERE r.estado = :estado")
    Page<ReservaEntity> buscarPorEstadoPaginado(@Param("estado") EstadoReserva estado, Pageable pageable);
}
