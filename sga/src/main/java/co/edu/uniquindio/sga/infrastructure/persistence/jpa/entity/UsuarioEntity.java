package co.edu.uniquindio.sga.infrastructure.persistence.jpa.entity;

import co.edu.uniquindio.sga.infrastructure.security.RolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Usuario")
@Getter @Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 80, unique = true)
    private String correo;

    @Column(name = "contrasenia", nullable = false, length = 80)
    private String contrasenia;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private RolUsuario rol;

    @Column(name = "documento", nullable = false, length = 20, unique = true)
    private String documento;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
