package co.edu.uniquindio.sga.infrastructure.rest;

import co.edu.uniquindio.sga.application.dto.request.CancelarReservaRequest;
import co.edu.uniquindio.sga.application.dto.request.CrearReservaRequest;
import co.edu.uniquindio.sga.application.dto.response.ReservaDetalleResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaResumenResponse;
import co.edu.uniquindio.sga.application.usecase.CancelarReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConfirmarReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarReservasPorEstadoUseCase;
import co.edu.uniquindio.sga.application.usecase.CrearReservaUseCase;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.infrastructure.rest.mapper.ReservaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor  /** Genera el constructor con todos los campos final */
public class ReservaController {

    /** Casos de Uso - Dependencias - Mapper */
    private final CrearReservaUseCase crearReservaUseCase;
    private final ConsultarReservaUseCase consultarReservaUseCase;
    private final ConsultarReservasPorEstadoUseCase consultarReservasPorEstadoUseCase;
    private final ConfirmarReservaUseCase confirmarReservaUseCase;
    private final CancelarReservaUseCase cancelarReservaUseCase;
    private final ReservaMapper mapper;

    /** ------ Post - CREATE --------- */

    /** POST -> /api/reservas */
    @PostMapping
    public ResponseEntity<ReservaDetalleResponse> crearReserva(@Valid @RequestBody CrearReservaRequest request) {

        /**
         * 1. Convertir DTO a tipos del dominio y delegar al caso de uso.
         * La fecha actual la pone el servidor, nunca el cliente.
         */
        Reserva reserva = crearReservaUseCase.ejecutar(
                new IdentificacionApartamento(request.identificacionApartamento()),
                mapper.aEstancia(request),
                mapper.aHoraEstimadaLlegada(request.horaEstimadaLlegada()),
                mapper.aOcupante(request.titular()),
                mapper.aOcupantes(request.ocupantes()),
                request.canalOrigen(),
                LocalDate.now()
        );

        /** 2. Mapear de dominio a DTO */
        ReservaDetalleResponse response = mapper.aDetalle(reserva);

        /**
         * 3. 201 Created + header Location con la URL del recurso nuevo
         * http://localhost:8080/api/reservas/RES-2026-00001
         */
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(reserva.getCodigo().valor())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /** ------ Get - READ --------- */

    /** GET -> /api/reservas/{codigo} */
    @GetMapping("/{codigo}")
    public ResponseEntity<ReservaDetalleResponse> obtenerReserva(@PathVariable String codigo) {

        /** El String de la URL se convierte a tipo del dominio ANTES del caso de uso */
        Reserva reserva = consultarReservaUseCase.ejecutar(new CodigoReserva(codigo));
        return ResponseEntity.ok(mapper.aDetalle(reserva));
    }

    /** GET -> /api/reservas?estado=PENDIENTE */
    @GetMapping
    public ResponseEntity<List<ReservaResumenResponse>> listarReservas(@RequestParam EstadoReserva estado) {

        List<Reserva> reservas = consultarReservasPorEstadoUseCase.ejecutar(estado);
        return ResponseEntity.ok(mapper.aResumenes(reservas));
    }

    /**
     * ------ Put - OPERACIONES DE NEGOCIO ---------
     * El endpoint no recibe el estado final: el estado al que pasa lo decide el agregado Reserva
     */

    /** PUT -> /api/reservas/{codigo}/confirmar */
    @PutMapping("/{codigo}/confirmar")
    public ResponseEntity<ReservaDetalleResponse> confirmarReserva(@PathVariable String codigo) {

        Reserva reserva = confirmarReservaUseCase.ejecutar(new CodigoReserva(codigo));
        return ResponseEntity.ok(mapper.aDetalle(reserva));
    }

    /** PUT -> /api/reservas/{codigo}/cancelar */
    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<ReservaDetalleResponse> cancelarReserva(
            @PathVariable String codigo,
            @Valid @RequestBody CancelarReservaRequest request) {

        Reserva reserva = cancelarReservaUseCase.ejecutar(
                new CodigoReserva(codigo),
                request.motivo(),
                LocalDateTime.now()
        );
        return ResponseEntity.ok(mapper.aDetalle(reserva));
    }
}
