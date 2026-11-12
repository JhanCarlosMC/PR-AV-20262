package co.edu.uniquindio.sga.infrastructure.rest;

import co.edu.uniquindio.sga.application.dto.response.ApartamentoResumenResponse;
import co.edu.uniquindio.sga.application.usecase.ConsultarApartamentosUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/apartamentos")
@RequiredArgsConstructor
@Tag(name = "Apartamentos")
public class ApartamentoController {

    private final ConsultarApartamentosUseCase consultarApartamentos;

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Listar apartamentos activos")
    public List<ApartamentoResumenResponse> listar() {
        return consultarApartamentos.ejecutar();
    }
}
