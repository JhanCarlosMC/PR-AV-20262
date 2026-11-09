package co.edu.uniquindio.sga.application.exception;
public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException() { super("El correo o documento ya está registrado"); }
}
