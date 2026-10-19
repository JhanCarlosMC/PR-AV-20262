package co.edu.uniquindio.sga.infraestructure.rest.mapper;

import co.edu.uniquindio.sga.application.dto.request.CrearReservaRequest;
import co.edu.uniquindio.sga.application.dto.request.OcupanteRequest;
import co.edu.uniquindio.sga.application.dto.response.ReservaDetalleResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaResumenResponse;
import co.edu.uniquindio.sga.domain.entity.Ocupante;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    // --- Request a Dominio ----
    Estancia aEstancia(CrearReservaRequest request);

    HoraEstimadaLlegada aHoraEstimadaLlegada(LocalTime localTime);

    @Mapping(target = "nombreCompleto", source = "nombre")
    Ocupante aOcupante(OcupanteRequest request);

    List<Ocupante> aOcupantes(List<OcupanteRequest> requests);


    // --- Dominio a Response ----
    @Mapping(target = "identificacionApartamento", source = "apartamento")
    @Mapping(target = "fechaEntrada", source = "estancia.fechaEntrada")
    @Mapping(target = "fechaSalida", source = "estancia.fechaSalida")
    @Mapping(target = "noches", expression = "java(reserva.getEstancia().noches())")
    @Mapping(target = "totalOcupantes", expression = "java(reserva.totalOcupantes())")
    @Mapping(target = "valorTotal", source = "valorCongelado")
    @Mapping(target = "moneda", constant = "COP")
    @Mapping(target = "versionPoliticaCancelacion", source = "politicaCongelada")
    ReservaDetalleResponse aDetalle(Reserva reserva);

    @Mapping(target = "identificacionApartamento", source = "apartamento")
    @Mapping(target = "fechaEntrada", source = "estancia.fechaEntrada")
    @Mapping(target = "fechaSalida", source = "estancia.fechaSalida")
    @Mapping(target = "nombreTitular", source = "titular.getNombreCompleto()")
    @Mapping(target = "totalOcupantes", expression = "java(reserva.totalOcupantes())")
    @Mapping(target = "valorTotal", source = "valorCongelado")
    List<ReservaResumenResponse> aResumenes(List<Reserva> reservas);


    // --- Conversiones Objetos de Valor ----
    //String a DocumentoIdentidad(ObjetoValor)
    default DocumentoIdentidad aDocumento(String documento){
        return new DocumentoIdentidad(documento);
    }

    //DocumentoIdentidad(ObjetoValor) a String
    default String aTexto(DocumentoIdentidad documentoIdentidad){
        return documentoIdentidad.valor();
    }

    default IdentificacionApartamento aIdentificacionApartamento(String identificacionApartamento){
        return new IdentificacionApartamento(identificacionApartamento);
    }

    default String aTexto(IdentificacionApartamento aIdentificacionApartamento){
        return aIdentificacionApartamento.valor();
    }

    default BigDecimal aPesos(Dinero dinero){
        return dinero.valor();
    }


}
