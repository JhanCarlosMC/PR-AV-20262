package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;

import java.time.LocalDate;
import java.util.Optional;

public interface PoliticaCancelacionRepository {

    /** RN-22: la versión que rige en la fecha indicada es la que se congela al crear la reserva. */
    Optional<PoliticaCancelacion> obtenerVigenteEn(LocalDate fecha);

    /** RN-13: la retención se calcula con la versión congelada en la reserva, no con la vigente hoy. */
    Optional<PoliticaCancelacion> obtenerPorVersion(VersionPolitica version);
}
