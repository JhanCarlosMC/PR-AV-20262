package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Periodo;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity.ReservaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.mapper.ReservaPersistenceMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Adaptador JPA del puerto ReservaRepository (Guía 10, Parte 5) */
@Repository
@Transactional
@RequiredArgsConstructor
public class ReservaJpaRepository implements ReservaRepository {

    private final ReservaJpaDataRepository dataRepository;
    private final ReservaPersistenceMapper mapper;

    @Override
    public void guardar(Reserva reserva) {
        // 1. Convertir dominio → entidad
        ReservaEntity entity = mapper.toReservaEntity(reserva);

        // 2. El dominio no conoce el id de la BD: si la reserva ya existe, se reutiliza
        //    su id para que save() actualice la fila en lugar de insertar otra
        dataRepository.findByCodigo(entity.getCodigo())
                .ifPresent(existente -> entity.setId(existente.getId()));

        // 3. Guardar en H2 (aquí sí se dice "save": es vocabulario de Spring Data)
        dataRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo) {
        return dataRepository.findByCodigo(codigo.valor())
                .map(mapper::toReservaDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarPorEstado(EstadoReserva estado) {
        return dataRepository.findByEstado(estado)
                .stream()
                .map(mapper::toReservaDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento, Periodo periodo) {
        // Igual que la versión en memoria: trae las activas del apartamento y el servicio
        // de disponibilidad revisa el solape de fechas. Filtrar por fechas en la consulta: Guía 11
        List<EstadoReserva> activas = Arrays.stream(EstadoReserva.values())
                .filter(EstadoReserva::esActiva)
                .toList();

        return dataRepository.findByApartamentoIdAndEstadoIn(apartamento.valor(), activas)
                .stream()
                .map(mapper::toReservaDomain)
                .toList();
    }
}
