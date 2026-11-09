package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.application.dto.request.LoginRequest;
import co.edu.uniquindio.sga.application.dto.response.TokenResponse;
import co.edu.uniquindio.sga.application.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Implementación del inicio de sesión con Spring Security (Guía 12) */
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final co.edu.uniquindio.sga.infrastructure.persistence.jpa.UsuarioJpaDataRepository repository;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;

    @Override
    public void registrar(co.edu.uniquindio.sga.application.dto.request.RegistroRequest request) {
        String correo = request.correo().trim().toLowerCase(java.util.Locale.ROOT);
        String documento = request.documento().trim();
        if (repository.existsByCorreo(correo) || repository.existsByDocumento(documento))
            throw new co.edu.uniquindio.sga.application.exception.UsuarioDuplicadoException();
        var usuario = new co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity.UsuarioEntity();
        usuario.setNombre(request.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setContrasena(encoder.encode(request.contrasena()));
        usuario.setTelefono(request.telefono().trim());
        usuario.setDocumento(documento);
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setRol(RolUsuario.HUESPED);
        try { repository.saveAndFlush(usuario); }
        catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new co.edu.uniquindio.sga.application.exception.UsuarioDuplicadoException();
        }
    }

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${sga.jwt.expiracion-minutos}")
    private long expiracionMinutos;

    @Override
    public TokenResponse login(LoginRequest request) {

        // 1. Busca el usuario (UserConfig) y compara la contraseña con BCrypt.
        //    Si falla, lanza una AuthenticationException → 401 (sección 6.4)
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.correo().trim().toLowerCase(java.util.Locale.ROOT), request.contrasena())
        );

        // 2. Credenciales correctas: se leen el rol y el documento del usuario autenticado
        CustomUserDetails usuario = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 3. Se calcula la vigencia y se firma el token
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(expiracionMinutos, ChronoUnit.MINUTES);

        String token = jwtTokenProvider.generateTokenAsString(
                authentication.getName(), usuario.usuario().getDocumento(), roles, ahora, expira);

        return new TokenResponse(token, "Bearer", expira, roles);
    }
}
