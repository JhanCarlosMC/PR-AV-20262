package co.edu.uniquindio.sga.infrastructure.persistence.jpa.mapper;

import co.edu.uniquindio.sga.domain.entity.Ocupante;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.*;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity.OcupanteEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity.ReservaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaPersistenceMapper {


    // ------ Dominio a Entity ---------

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", source = "codigo.valor")
    @Mapping(target = "apartamentoId", source = "apartamento.valor")
    @Mapping(target = "documentoTitular", source = "titular.documento.valor")
    @Mapping(target = "horaEstimadaLlegada", source = "horaEstimadaLlegada.hora")
    @Mapping(target = "valorCongelado", source = "valorCongelado.valor")
    ReservaEntity toReservaEntity(Reserva reserva);

    @Mapping(target = "documento", source = "documento.valor")
    OcupanteEmbeddable toOcupanteEmbeddable(Ocupante ocupante);

    // ------ Entity a Dominio ---------

    default Ocupante toOcupanteDomain(OcupanteEmbeddable ocupanteEmbeddable) {
        return new Ocupante(
                new DocumentoIdentidad(ocupanteEmbeddable.documento()),
                ocupanteEmbeddable.nombreCompleto(),
                ocupanteEmbeddable.fechaNacimiento());
    }

    default Reserva toReservaDomain(ReservaEntity reservaEntity) {

        //1. Reconstruir los ocupantes y el titular
        List<Ocupante> ocupantes = reservaEntity.getOcupantes().stream()
                .map(this::toOcupanteDomain)
                .toList();

        Ocupante titular = ocupantes.stream()
                .filter(ocupante ->
                        ocupante.getDocumento().valor().equals(reservaEntity.getDocumentoTitular()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("El titular no esta dentro de los ocupantes de la reserva"));

        //Realizar la reconstruccion de la reserva
        return Reserva.recontruir(
                new CodigoReserva(reservaEntity.getCodigo()),
                new IdentificacionApartamento(reservaEntity.getApartamentoId()),
                new Estancia(reservaEntity.getEstancia().fechaEntrada(), reservaEntity.getEstancia().fechaSalida()),
                reservaEntity.getEstado(),
                reservaEntity.getCanalOrigen(),
                titular, ocupantes,
                new HoraEstimadaLlegada(reservaEntity.getHoraEstimadaLlegada()),
                new Dinero(reservaEntity.getValorCongelado()),
                new VersionPolitica(reservaEntity.getPoliticaCongelada().numero(), reservaEntity.getPoliticaCongelada().vigenteDesde()),
                reservaEntity.getFechaCreacion(),
                reservaEntity.getMotivoCancelacion()
        );
    }

}
