package co.edu.uniquindio.sga.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/** Cadena de filtros de seguridad del SGA (Guía 12) */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @org.springframework.beans.factory.annotation.Value("${sga.seguridad.herramientas-desarrollo:true}")
    private boolean herramientasDesarrollo;

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // Permite que la consola de H2 se muestre en un iframe del mismo origen
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> {
                    if (herramientasDesarrollo) auth.requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll();
                    auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/registro",
                                "/error",
                                "/actuator/health"
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/apartamentos/*/estado-operativo").hasAnyAuthority("ADMINISTRADOR", "PERSONAL_SERVICIO")
                        .requestMatchers("/api/temporadas/**", "/api/tarifas/**", "/api/apartamentos/**", "/api/bloqueos/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/api/reservas/**").authenticated()
                        .anyRequest().denyAll();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
