package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.HoraEstimadaLlegada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Raíz del agregado Reserva.
 *
 * <p>Invariantes que garantiza este agregado: RN-02 (capacidad del apartamento al crear y
 * modificar), RN-03 (a través de {@link Estancia}), RN-04 (no se reserva hacia el pasado),
 * RN-06 (ocupantes facturables), RN-08 (transiciones de estado), RN-09 (hora estimada de
 * llegada antes de confirmar), RN-10 (registro de llegada), RN-14 (revalidación al modificar)
 * y RN-22 (valor y política congelados al crear).
 */
public class Reserva {

    private final CodigoReserva codigo;
    private final IdentificacionApartamento apartamento;
    private Estancia estancia;
    private EstadoReserva estado;
    private final CanalOrigen canalOrigen;
    private IdentificadorExterno identificadorExterno;
    private final Ocupante titular;
    private List<Ocupante> ocupantes;
    private HoraEstimadaLlegada horaEstimadaLlegada;
    private Dinero valorCongelado;
    private VersionPolitica politicaCongelada;
    private final LocalDate fechaCreacion;
    private String motivoCancelacion;

    private Reserva(CodigoReserva codigo,
                     IdentificacionApartamento apartamento,
                     Estancia estancia,
                     CanalOrigen canalOrigen,
                     Ocupante titular,
                     List<Ocupante> ocupantes,
                     Dinero valorCongelado,
                     VersionPolitica politicaCongelada,
                     LocalDate fechaCreacion) {
        this.codigo = codigo;
        this.apartamento = apartamento;
        this.estancia = estancia;
        this.estado = EstadoReserva.PENDIENTE;
        this.canalOrigen = canalOrigen;
        this.titular = titular;
        this.ocupantes = ocupantes;
        this.valorCongelado = valorCongelado;
        this.politicaCongelada = politicaCongelada;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Nota de diseño: la capacidad entra como parámetro y no se consulta llamando a un
     * objeto {@code Apartamento}, porque eso implicaría que este agregado lea el estado
     * interno de otro agregado, lo cual está prohibido. Quien orquesta la creación es
     * responsable de traer ese dato.
     */
    public static Reserva crear(CodigoReserva codigo,
                                 IdentificacionApartamento apartamento,
                                 int capacidadApartamento,
                                 Estancia estancia,
                                 Ocupante titular,
                                 List<Ocupante> ocupantes,
                                 CanalOrigen canalOrigen,
                                 Dinero valorCongelado,
                                 VersionPolitica politicaCongelada,
                                 LocalDate fechaActual) {
        if (codigo == null) {
            throw new ReglaDominioException("La reserva debe tener un código.");
        }
        if (apartamento == null) {
            throw new ReglaDominioException("La reserva debe indicar el apartamento.");
        }
        if (estancia == null) {
            throw new ReglaDominioException("La reserva debe indicar la estancia.");
        }
        if (titular == null) {
            throw new ReglaDominioException("La reserva debe tener un titular.");
        }
        if (ocupantes == null || ocupantes.isEmpty()) {
            throw new ReglaDominioException("La reserva debe tener al menos un ocupante.");
        }
        if (canalOrigen == null) {
            throw new ReglaDominioException("La reserva debe indicar el canal de origen.");
        }
        if (valorCongelado == null) {
            throw new ReglaDominioException("La reserva debe tener un valor calculado.");
        }
        if (politicaCongelada == null) {
            throw new ReglaDominioException("La reserva debe tener una política de cancelación vigente.");
        }
        if (fechaActual == null) {
            throw new ReglaDominioException("Se debe indicar la fecha actual para crear la reserva.");
        }
        if (!ocupantes.contains(titular)) {
            throw new ReglaDominioException("El titular debe estar entre los ocupantes de la reserva.");
        }
        for (int i = 0; i < ocupantes.size(); i++) {
            for (int j = i + 1; j < ocupantes.size(); j++) {
                if (ocupantes.get(i).equals(ocupantes.get(j))) {
                    throw new ReglaDominioException("La reserva no puede tener ocupantes repetidos.");
                }
            }
        }
        // RN-04: no se crean reservas hacia el pasado
        if (estancia.fechaEntrada().isBefore(fechaActual)) {
            throw new ReglaDominioException("La fecha de entrada no puede ser anterior a la fecha actual.");
        }
        // RN-02: el número total de ocupantes no puede exceder la capacidad del apartamento
        if (ocupantes.size() > capacidadApartamento) {
            throw new ReglaDominioException("El número de ocupantes excede la capacidad del apartamento.");
        }

        // RN-22: el valor y la política quedan congelados al crear la reserva
        return new Reserva(codigo, apartamento, estancia, canalOrigen, titular,
                List.copyOf(ocupantes), valorCongelado, politicaCongelada, fechaActual);
    }

    public int totalOcupantes() {
        return ocupantes.size();
    }

    /** RN-06: un ocupante es facturable si a la fecha de entrada alcanza el umbral configurado. */
    public int ocupantesFacturables(UmbralEdadFacturable umbral) {
        return (int) ocupantes.stream()
                .filter(ocupante -> ocupante.esFacturableEn(estancia, umbral))
                .count();
    }

    /** RN-09: toda reserva debe registrar hora estimada de llegada antes de confirmarse. */
    public void registrarHoraEstimadaLlegada(HoraEstimadaLlegada hora) {
        if (hora == null) {
            throw new ReglaDominioException("La hora estimada de llegada es obligatoria.");
        }
        this.horaEstimadaLlegada = hora;
    }

    public void confirmar() {
        if (this.estado != EstadoReserva.PENDIENTE) {
            throw new ReglaDominioException(
                    "Solo una reserva PENDIENTE puede confirmarse");                      // RN-08
        }
        if (this.horaEstimadaLlegada == null) {
            throw new ReglaDominioException(
                    "La reserva debe registrar hora estimada de llegada para confirmarse"); // RN-09
        }
        this.estado = EstadoReserva.CONFIRMADA;
    }

    // RN-08 (transición) y RN-12 (al dejar de estar activa libera sus noches de inmediato,
    // como consecuencia de que EstadoReserva.esActiva() pasa a ser falso, no por un campo aparte)
    public void cancelar(String motivo, LocalDate fechaActual) {
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException("La cancelación de una reserva debe indicar un motivo.");
        }
        transitarA(EstadoReserva.CANCELADA);
        this.motivoCancelacion = motivo;
    }

    /**
     * RN-08 y RN-10: solo se registra la llegada si la reserva está CONFIRMADA y no antes de
     * la fecha de entrada. La verificación del estado operativo del apartamento no va aquí:
     * es una precondición externa que resuelve el servicio de dominio correspondiente.
     */
    public void registrarLlegada(LocalDate fechaActual) {
        if (fechaActual.isBefore(estancia.fechaEntrada())) {
            throw new ReglaDominioException("No se puede registrar la llegada antes de la fecha de entrada.");
        }
        transitarA(EstadoReserva.EN_CURSO);
    }

    public void registrarSalida(LocalDate fechaActual) {
        transitarA(EstadoReserva.FINALIZADA);
    }

    public void declararNoShow(LocalDate fechaActual) {
        transitarA(EstadoReserva.NO_SHOW);
    }

    /**
     * RN-14: todo cambio de fechas, ocupantes o apartamento revalida las condiciones de
     * creación y recalcula el valor. RN-22: el valor solo cambia por modificación explícita.
     */
    public void modificarEstancia(Estancia nueva, Dinero nuevoValor, int capacidadApartamento, LocalDate fechaActual) {
        if (nueva == null) {
            throw new ReglaDominioException("La nueva estancia es obligatoria.");
        }
        if (nuevoValor == null) {
            throw new ReglaDominioException("El nuevo valor recalculado es obligatorio.");
        }
        // RN-04: no se admite mover la reserva hacia el pasado
        if (nueva.fechaEntrada().isBefore(fechaActual)) {
            throw new ReglaDominioException("La fecha de entrada no puede ser anterior a la fecha actual.");
        }
        // RN-02: la nueva estancia también debe respetar la capacidad del apartamento
        if (ocupantes.size() > capacidadApartamento) {
            throw new ReglaDominioException("El número de ocupantes excede la capacidad del apartamento.");
        }
        this.estancia = nueva;
        this.valorCongelado = nuevoValor;
    }

    /** RN-19: la combinación canal + identificador externo es única. */
    public void marcarComoExterna(IdentificadorExterno identificador) {
        if (identificador == null) {
            throw new ReglaDominioException("El identificador externo es obligatorio.");
        }
        this.identificadorExterno = identificador;
    }

    /** RN-01, RN-12: una reserva activa (PENDIENTE, CONFIRMADA o EN_CURSO) ocupa noches. */
    public boolean estaActiva() {
        return estado.esActiva();
    }

    private void transitarA(EstadoReserva destino) {
        // RN-08: la reserva solo transita entre los estados permitidos
        if (!estado.puedeTransitarA(destino)) {
            throw new ReglaDominioException(
                    "No es posible pasar la reserva de " + estado + " a " + destino + ".");
        }
        estado = destino;
    }

    public CodigoReserva getCodigo() {
        return codigo;
    }

    public IdentificacionApartamento getApartamento() {
        return apartamento;
    }

    public Estancia getEstancia() {
        return estancia;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public CanalOrigen getCanalOrigen() {
        return canalOrigen;
    }

    public IdentificadorExterno getIdentificadorExterno() {
        return identificadorExterno;
    }

    public Ocupante getTitular() {
        return titular;
    }

    public List<Ocupante> getOcupantes() {
        return List.copyOf(ocupantes);
    }

    public HoraEstimadaLlegada getHoraEstimadaLlegada() {
        return horaEstimadaLlegada;
    }

    public Dinero getValorCongelado() {
        return valorCongelado;
    }

    public VersionPolitica getPoliticaCongelada() {
        return politicaCongelada;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva reserva)) return false;
        return Objects.equals(codigo, reserva.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
