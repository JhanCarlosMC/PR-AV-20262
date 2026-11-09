package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findBycorreo(String correo);
}
