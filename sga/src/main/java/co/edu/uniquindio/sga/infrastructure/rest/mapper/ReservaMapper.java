package co.edu.uniquindio.sga.infrastructure.rest.mapper;

import co.edu.uniquindio.sga.application.dto.request.CrearReservaRequest;
import co.edu.uniquindio.sga.application.dto.request.OcupanteRequest;
import co.edu.uniquindio.sga.application.dto.response.OcupanteResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaDetalleResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaResumenResponse;
import co.edu.uniquindio.sga.domain.entity.Ocupante;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.DocumentoIdentidad;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.HoraEstimadaLlegada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    /** ==================== REQUEST → DOMINIO ==================== */

    /**
     * Mismos nombres en el DTO y en el record Estancia: MapStruct usa su constructor,
     * y el constructor verifica RN-03. El mapper no valida nada por su cuenta.
     */
    Estancia aEstancia(CrearReservaRequest request);

    /** Ocupante se construye con su constructor público (documento, nombreCompleto, fechaNacimiento) */
    @Mapping(target = "nombreCompleto", source = "nombre")
    Ocupante aOcupante(OcupanteRequest request);

    List<Ocupante> aOcupantes(List<OcupanteRequest> requests);

    /** Opcional al crear (RN-09): si no llega, no se construye el objeto de valor */
    default HoraEstimadaLlegada aHoraEstimadaLlegada(LocalTime hora) {
        return hora == null ? null : new HoraEstimadaLlegada(hora);
    }

    /** ==================== DOMINIO → RESPONSE ==================== */

    @Mapping(target = "identificacionApartamento", source = "apartamento")
    @Mapping(target = "fechaEntrada", source = "estancia.fechaEntrada")
    @Mapping(target = "fechaSalida", source = "estancia.fechaSalida")
    @Mapping(target = "noches", expression = "java(reserva.getEstancia().noches())")
    @Mapping(target = "horaEstimadaLlegada", source = "horaEstimadaLlegada.hora")
    @Mapping(target = "totalOcupantes", expression = "java(reserva.totalOcupantes())")
    @Mapping(target = "valorTotal", source = "valorCongelado")
    @Mapping(target = "moneda", constant = "COP")
    @Mapping(target = "versionPoliticaCancelacion", source = "politicaCongelada.numero")
    ReservaDetalleResponse aDetalle(Reserva reserva);

    /** Las @Mapping van en el método de UN elemento; el de la lista solo lo recorre */
    @Mapping(target = "identificacionApartamento", source = "apartamento")
    @Mapping(target = "fechaEntrada", source = "estancia.fechaEntrada")
    @Mapping(target = "fechaSalida", source = "estancia.fechaSalida")
    @Mapping(target = "nombreTitular", source = "titular.nombreCompleto")
    @Mapping(target = "totalOcupantes", expression = "java(reserva.totalOcupantes())")
    @Mapping(target = "valorTotal", source = "valorCongelado")
    ReservaResumenResponse aResumen(Reserva reserva);

    List<ReservaResumenResponse> aResumenes(List<Reserva> reservas);

    @Mapping(target = "nombre", source = "nombreCompleto")
    OcupanteResponse aOcupanteResponse(Ocupante ocupante);

    /**
     * ==================== CONVERSIONES DE OBJETOS DE VALOR ====================
     * MapStruct las usa solo cada vez que necesita pasar de un tipo al otro.
     */

    default DocumentoIdentidad aDocumento(String valor) {
        return new DocumentoIdentidad(valor);
    }

    default String aTexto(DocumentoIdentidad documento) {
        return documento.valor();
    }

    default String aTexto(CodigoReserva codigo) {
        return codigo.valor();
    }

    default String aTexto(IdentificacionApartamento identificacion) {
        return identificacion.valor();
    }

    /** Dinero viaja como un entero de pesos, nunca como double (3.4) */
    default long aPesos(Dinero dinero) {
        return dinero.valor().setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
