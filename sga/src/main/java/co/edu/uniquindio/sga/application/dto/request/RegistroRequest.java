package co.edu.uniquindio.sga.application.dto.request;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record RegistroRequest(
    @NotBlank @Size(max=100) String nombre,
    @NotBlank @Email @Size(max=120) String correo,
    @NotBlank @Size(min=8, max=72) @Pattern(regexp="^(?=.*[A-Z])(?=.*[0-9]).{8,72}$", message="Debe contener mayúscula y número") String contrasena,
    @NotBlank @Size(max=20) String telefono,
    @NotBlank @Size(max=15) String documento,
    @NotNull @Past LocalDate fechaNacimiento
) {}
