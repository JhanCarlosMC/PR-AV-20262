package co.edu.uniquindio.sga.domain.valueobject;

public enum EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    EN_CURSO,
    FINALIZADA,
    CANCELADA,
    NO_SHOW;

    /** RN-01, RN-12: activas son PENDIENTE, CONFIRMADA o EN_CURSO. */
    public boolean esActiva() {
        return this == PENDIENTE || this == CONFIRMADA || this == EN_CURSO;
    }

    public boolean esTerminal() {
        return this == FINALIZADA || this == CANCELADA || this == NO_SHOW;
    }

    /** RN-08: la reserva solo transita entre los estados permitidos; toda transición inválida se rechaza. */
    public boolean puedeTransitarA(EstadoReserva destino) {
        if (this == PENDIENTE) {
            return destino == CONFIRMADA || destino == CANCELADA;
        }
        if (this == CONFIRMADA) {
            return destino == EN_CURSO || destino == CANCELADA || destino == NO_SHOW;
        }
        if (this == EN_CURSO) {
            return destino == FINALIZADA;
        }
        // FINALIZADA, CANCELADA y NO_SHOW son estados terminales: no admiten ninguna transición
        return false;
    }
}
