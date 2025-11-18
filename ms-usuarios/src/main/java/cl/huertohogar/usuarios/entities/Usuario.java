package cl.huertohogar.usuarios.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa la tabla 'usuarios' en la base de datos.
 * Implementa UserDetails para integrarse con Spring Security.
 */
@Data // Lombok: Genera automáticamente getters, setters, toString, equals y hashCode
@Builder // Lombok: Patrón de diseño Builder para construir objetos
@NoArgsConstructor // Lombok: Constructor vacío
@AllArgsConstructor // Lombok: Constructor con todos los argumentos
@Entity // Indica que esta clase es una entidad JPA
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"), // El email debe ser único
        @UniqueConstraint(columnNames = "run")     // El RUN debe ser único
})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String run;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Esta será la contraseña encriptada

    private String region;
    private String comuna;
    private String direccion;

    // Usamos @Enumerated para guardar el rol como String en la BD (ej. "ADMIN" o "CLIENTE")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // --- MÉTODOS DE USERDETAILS (Spring Security) ---

    /**
     * Retorna los permisos/roles del usuario.
     * Spring Security usará esto para verificar la autorización (ej. @PreAuthorize("hasRole('ADMIN')"))
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Le asignamos el rol (ej. "ROLE_ADMIN", "ROLE_CLIENTE")
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        // Le decimos a Spring Security que nuestro "username" es el email.
        return this.email;
    }

    @Override
    public String getPassword() {
        // Retorna la contraseña. Spring Security la usará para compararla con la que envía el usuario.
        return this.password;
    }

    // Los siguientes métodos los dejamos en 'true' para indicar que la cuenta está activa.
    // Podríamos añadir lógica aquí para bloquear cuentas, hacer que las contraseñas expiren, etc.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}