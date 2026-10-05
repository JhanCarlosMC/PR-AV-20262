package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.HorarioAlojamiento;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * RN-13: la retención por cancelación y por no-show se determina con la versión de política
 * congelada en la reserva (RN-22), no con la vigente al momento del hecho.
 */
public class RetencionService {

    // L-12: la antelación se mide contra la hora de entrada configurada
    private final HorarioAlojamiento horario;

    public RetencionService(HorarioAlojamiento horario) {
        if (horario == null) {
            throw new ReglaDominioException("El horario del alojamiento es obligatorio.");
        }
        this.horario = horario;
    }

    public Dinero porCancelacion(Reserva reserva, PoliticaCancelacion politica, LocalDateTime momento) {
        verificarPoliticaCongelada(reserva, politica);
        LocalDateTime inicioEstancia = reserva.getEstancia().fechaEntrada().atTime(horario.horaEntrada());
        long horasAntelacion = Math.max(0, ChronoUnit.HOURS.between(momento, inicioEstancia));
        return politica.retencionPara(horasAntelacion).aplicarA(reserva.getValorCongelado());
    }

    public Dinero porNoShow(Reserva reserva, PoliticaCancelacion politica) {
        verificarPoliticaCongelada(reserva, politica);
        return politica.retencionPorNoShow().aplicarA(reserva.getValorCongelado());
    }

    private void verificarPoliticaCongelada(Reserva reserva, PoliticaCancelacion politica) {
        if (!politica.getVersion().equals(reserva.getPoliticaCongelada())) {
            throw new ReglaDominioException(
                    "La retención debe calcularse con la versión de política congelada en la reserva.");
        }
    }
}
