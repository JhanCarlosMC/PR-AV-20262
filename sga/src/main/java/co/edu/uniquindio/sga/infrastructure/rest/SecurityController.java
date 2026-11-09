package co.edu.uniquindio.sga.infrastructure.rest;

import co.edu.uniquindio.sga.application.dto.request.LoginRequest;
import co.edu.uniquindio.sga.application.dto.response.TokenResponse;
import co.edu.uniquindio.sga.application.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller REST de autenticación (Guía 12) */
@io.swagger.v3.oas.annotations.security.SecurityRequirements
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión y emisión del JWT")
public class SecurityController {

    @PostMapping("/registro")
    @Operation(summary = "Registrar un huésped", security = {})
    @ApiResponse(responseCode = "201", description = "Huésped registrado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Correo o documento duplicado")
    public ResponseEntity<Void> registrar(@Valid @RequestBody co.edu.uniquindio.sga.application.dto.request.RegistroRequest request) {
        securityService.registrar(request);
        return ResponseEntity.status(201).build();
    }

    private final SecurityService securityService;

    /** POST -> /api/auth/login */
    @PostMapping("/login")
    @SecurityRequirements    // este endpoint es público: no exige token en Swagger
    @Operation(summary = "Iniciar sesión", description = "Devuelve un JWT para usar como Bearer token")
    @ApiResponse(responseCode = "200", description = "Token emitido")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario inactivo")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(securityService.login(request));
    }
}
