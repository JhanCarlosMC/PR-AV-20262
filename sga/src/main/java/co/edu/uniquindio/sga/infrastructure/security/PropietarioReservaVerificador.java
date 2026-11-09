package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/** Verifica que el usuario autenticado sea el titular de la reserva (Guía 12, 7.2) */
@Component("propietarioReserva")
@RequiredArgsConstructor
public class PropietarioReservaVerificador {

    private final ReservaRepository reservaRepository;

    // Canal y titular son reglas de acceso: el dominio permanece independiente del usuario.
    public boolean puedeCrear(co.edu.uniquindio.sga.application.dto.request.CrearReservaRequest request, Authentication authentication) {
        var roles = authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        if (roles.contains("ADMINISTRADOR")) return true;
        if (roles.contains("RECEPCIONISTA")) return request.canalOrigen() == co.edu.uniquindio.sga.domain.valueobject.CanalOrigen.DIRECTO;
        return roles.contains("HUESPED") && authentication.getPrincipal() instanceof Jwt jwt
            && request.canalOrigen() == co.edu.uniquindio.sga.domain.valueobject.CanalOrigen.PORTAL
            && request.titular() != null && request.titular().documento().equals(jwt.getClaimAsString("documento"));
    }

    public boolean esTitular(String codigo, Authentication authentication) {

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }
        String documento = jwt.getClaimAsString("documento");

        // Si la reserva no existe, también se niega: un huésped no puede
        // averiguar qué códigos de reserva existen probando con 403 frente a 404
        return reservaRepository.obtenerPorCodigo(new CodigoReserva(codigo))
                .map(reserva -> reserva.getTitular().getDocumento().valor().equals(documento))
                .orElse(false);
    }
}
