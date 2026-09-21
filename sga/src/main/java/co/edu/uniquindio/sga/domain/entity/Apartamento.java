package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Raíz del agregado Apartamento.
 */
public class Apartamento {

    private final IdentificacionApartamento identificacion;
    private String nombre;
    private int dormitorios;
    private int capacidad;
    private EstadoOperativo estadoOperativo;
    private boolean activo;
    // RN de oro #9: colección interna siempre de solo lectura; toda modificación reemplaza la referencia
    private List<Tarifa> tarifas = List.of();

    public Apartamento(IdentificacionApartamento identificacion, String nombre, int dormitorios, int capacidad) {
        if (identificacion == null) {
            throw new ReglaDominioException("El apartamento debe tener una identificación.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El apartamento debe tener un nombre.");
        }
        if (dormitorios < 1) {
            throw new ReglaDominioException("El apartamento debe tener al menos un dormitorio.");
        }
        if (capacidad < 1) {
            throw new ReglaDominioException("El apartamento debe tener capacidad para al menos un ocupante.");
        }
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.dormitorios = dormitorios;
        this.capacidad = capacidad;
        this.estadoOperativo = EstadoOperativo.PREPARADO;
        this.activo = true;
    }

    /** RN-02: la capacidad es un tope rígido, sin excepciones. Todos los ocupantes cuentan. */
    public boolean admite(int totalOcupantes) {
        return totalOcupantes <= capacidad;
    }

    /** RN-11: solo puede recibir un grupo si está activo y en estado operativo PREPARADO. */
    public boolean puedeRecibirGrupo() {
        return activo && estadoOperativo.permiteRegistro();
    }

    /** RN-05: tarifa vigente del apartamento para una temporada dada. */
    public Optional<Tarifa> tarifaEn(IdTemporada temporada) {
        return tarifas.stream()
                .filter(tarifa -> tarifa.temporada().equals(temporada))
                .findFirst();
    }

    public void definirTarifa(Tarifa tarifa) {
        if (tarifa == null) {
            throw new ReglaDominioException("La tarifa a definir es obligatoria.");
        }
        if (tarifaEn(tarifa.temporada()).isPresent()) {
            throw new ReglaDominioException("Ya existe una tarifa definida para esa temporada.");
        }
        List<Tarifa> actualizadas = new ArrayList<>(tarifas);
        actualizadas.add(tarifa);
        this.tarifas = List.copyOf(actualizadas);
    }

    // F-09: solo se ocupa un apartamento que está PREPARADO
    public void marcarOcupado() {
        if (estadoOperativo != EstadoOperativo.PREPARADO) {
            throw new ReglaDominioException("Solo se puede ocupar un apartamento que está PREPARADO.");
        }
        estadoOperativo = EstadoOperativo.OCUPADO;
    }

    // F-09: tras la salida del grupo, el apartamento OCUPADO queda pendiente de preparación
    public void marcarPendientePreparacion() {
        if (estadoOperativo != EstadoOperativo.OCUPADO) {
            throw new ReglaDominioException(
                    "Solo se puede marcar pendiente de preparación un apartamento OCUPADO.");
        }
        estadoOperativo = EstadoOperativo.PENDIENTE_PREPARACION;
    }

    // F-09: la preparación solo inicia si el apartamento estaba pendiente de ella
    public void marcarEnPreparacion() {
        if (estadoOperativo != EstadoOperativo.PENDIENTE_PREPARACION) {
            throw new ReglaDominioException(
                    "Solo se puede iniciar la preparación de un apartamento PENDIENTE_PREPARACION.");
        }
        estadoOperativo = EstadoOperativo.EN_PREPARACION;
    }

    // F-09: queda preparado al terminar la preparación, o al volver de estar fuera de servicio
    public void marcarPreparado() {
        if (estadoOperativo != EstadoOperativo.EN_PREPARACION && estadoOperativo != EstadoOperativo.FUERA_DE_SERVICIO) {
            throw new ReglaDominioException(
                    "Solo se puede preparar un apartamento que está EN_PREPARACION o FUERA_DE_SERVICIO.");
        }
        estadoOperativo = EstadoOperativo.PREPARADO;
    }

    // F-09: se puede declarar fuera de servicio desde cualquier estado, salvo que ya lo esté
    public void declararFueraDeServicio() {
        if (estadoOperativo == EstadoOperativo.FUERA_DE_SERVICIO) {
            throw new ReglaDominioException("El apartamento ya está fuera de servicio.");
        }
        estadoOperativo = EstadoOperativo.FUERA_DE_SERVICIO;
    }

    // La eliminación de apartamentos es lógica, nunca física
    public void desactivar() {
        activo = false;
    }

    public IdentificacionApartamento getIdentificacion() {
        return identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDormitorios() {
        return dormitorios;
    }

    // Cambiar la capacidad no afecta las reservas ya creadas: su valor y ocupantes quedaron
    // congelados al crearlas (RN-22) y no vuelven a consultar este campo.
    public int getCapacidad() {
        return capacidad;
    }

    public EstadoOperativo getEstadoOperativo() {
        return estadoOperativo;
    }

    public boolean estaActivo() {
        return activo;
    }

    public List<Tarifa> getTarifas() {
        return tarifas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Apartamento that)) return false;
        return Objects.equals(identificacion, that.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identificacion);
    }
}
