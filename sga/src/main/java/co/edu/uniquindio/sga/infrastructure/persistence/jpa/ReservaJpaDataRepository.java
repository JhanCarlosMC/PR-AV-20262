package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaJpaDataRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByEstado(EstadoReserva estado);
    Optional<ReservaEntity> findByCodigo(String codigo);

    List<ReservaEntity> findByApartamentoIdAndEstadoIn(String apartamentoId, Collection<EstadoReserva> estados);

}
