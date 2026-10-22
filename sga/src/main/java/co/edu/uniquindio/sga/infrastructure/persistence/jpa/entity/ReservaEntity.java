package co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CollectionId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Reserva")
@Getter
@Setter
@NoArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 15)
    private String codigo;

    @Column(name = "apartamento_id", nullable = false, length = 15)
    private String apartamentoId;

    @Embedded //Este elemento se vuelve columnas de la tabla reserva
    private EstanciaEmbeddable estancia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 12)
    private EstadoReserva estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_origen", nullable = false, length = 12)
    private CanalOrigen canalOrigen;

    @Column(name = "documento_titular", nullable = false, length = 15)
    private String documentoTitular;

    @CollectionTable(name = "reserva_ocupantes", //Creaacion de tabla
                    joinColumns = @JoinColumn(name = "reserva_id")) //Relacion llave foranea
    private List<OcupanteEmbeddable> ocupantes = new ArrayList<>();

    @Column(name = "hora_estimada_llegada")
    private LocalTime horaEstimadaLlegada;

    @Column(name = "valor_congelado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorCongelado;

    @Embedded
    private PoliticaEmbeddable politicaCongelada;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "motivo_cancelacion", length = 300)
    private String motivoCancelacion;
}
