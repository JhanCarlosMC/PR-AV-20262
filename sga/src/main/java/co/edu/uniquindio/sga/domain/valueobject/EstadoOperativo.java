package co.edu.uniquindio.sga.domain.valueobject;

public enum EstadoOperativo {
    PREPARADO,
    OCUPADO,
    PENDIENTE_PREPARACION,
    EN_PREPARACION,
    FUERA_DE_SERVICIO;

    /** RN-11: un apartamento solo puede recibir un grupo si su estado operativo es PREPARADO. */
    public boolean permiteRegistro() {
        return this == PREPARADO;
    }
}
