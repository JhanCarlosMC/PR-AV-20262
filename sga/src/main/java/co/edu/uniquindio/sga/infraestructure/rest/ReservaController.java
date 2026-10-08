package co.edu.uniquindio.sga.infraestructure.rest;

import co.edu.uniquindio.sga.application.dto.request.CancelarReservaRequest;
import co.edu.uniquindio.sga.application.dto.request.CrearReservaRequest;
import co.edu.uniquindio.sga.application.dto.response.ReservaDetalleResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaResumenResponse;
import co.edu.uniquindio.sga.application.usecase.*;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.infraestructure.rest.mapper.ReservaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    //Casos de Uso - Dependencias - Mapper
    private final CancelarReservaUseCase cancelarReservaUseCase;
    private final ConfirmarReservaUseCase confirmarReservaUseCase;
    private final ConsultarReservaUseCase consultarReservaUseCase;
    private final ConsultarReservasPorEstadoUseCase consultarReservasPorEstadoUseCase;
    private final CrearReservaUseCase crearReservaUseCase;
    private final ReservaMapper mapper;


    //------ Get - READ ---------

    //GET -> api/reservas/{codigo}
    @GetMapping("/{codigo}")
    public ResponseEntity<ReservaDetalleResponse> obtenerReserva(@PathVariable String codigo){

        Reserva reserva = consultarReservaUseCase.ejecutar(new CodigoReserva(codigo));

        return ResponseEntity.ok(mapper.aDetalle(reserva));
    }

    //GET -> /api/reservas?estado={PENDIENTE/CONFIMADA/...}
    @GetMapping
    public ResponseEntity<List<ReservaResumenResponse>> listarReservas(@RequestParam EstadoReserva estado){

        List<Reserva> reservas = consultarReservasPorEstadoUseCase.ejecutar(estado);

        return ResponseEntity.ok(mapper.aResumenes(reservas));
    }

    //------ Post - CREATE ---------

    //POST -> /api/reservas
    @PostMapping
    public ResponseEntity<ReservaDetalleResponse> crearReserva(@Valid @RequestBody CrearReservaRequest request){

        //1. Convertir DTO a Tipos de datos del dominio
        //2. Delegar a el caso de uso
        Reserva reserva = crearReservaUseCase.ejecutar(
                new IdentificacionApartamento(request.identificacionApartamento()),
                mapper.aEstancia(request),
                mapper.aHoraEstimadaLlegada(request.horaEstimadaLlegada()),
                mapper.aOcupante(request.titular()),
                mapper.aOcupantes(request.ocupantes()),
                request.canalOrigen(),
                LocalDate.now()
        );

        //3. Mapear de Dominio a DTO
        ReservaDetalleResponse response = mapper.aDetalle(reserva);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(reserva.getCodigo().valor())
                .toUri();
        //http://localhost:8080/api/reservas/RES-01

        //4. Respuesta al cliente
        return ResponseEntity.created(location).body(response);
    }

    //------ Put - UPDATE ---------
    // Operacion de Negocio - Endpoint no recibe el estado final,
    // el estado al que se cambio lo decide el agregado de reserva

    //PUT -> /api/reservas/RES-01/cancelar
    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<ReservaDetalleResponse> cancelarReserva(
            @PathVariable String codigo,
            @Valid @RequestBody CancelarReservaRequest request){

        Reserva reserva = cancelarReservaUseCase.ejecutar(
                new CodigoReserva(codigo),
                request.motivo(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(mapper.aDetalle(reserva));
    }



}
