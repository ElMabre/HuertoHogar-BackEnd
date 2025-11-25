package cl.huertohogar.catalogo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Entity 
// Definimos constraints únicos a nivel de BD para asegurar integridad (nadie puede registrarse con el mismo email o RUN dos veces).
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"), 
        @UniqueConstraint(columnNames = "run")
})
// Implementar UserDetails es obligatorio para que Spring Security sepa usar nuestra clase "Usuario" para autenticar.
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
    private String password; // Aquí se guarda el Hash (BCrypt), nunca la contraseña plana.

    private String region;
    private String comuna;
    private String direccion;

    // Guardamos el rol como String ("ADMIN", "CLIENTE") en la base de datos para que sea legible.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // --- INTEGRACIÓN CON SPRING SECURITY ---

    /**
     * Transforma nuestro Enum 'Rol' en una 'GrantedAuthority' que Spring entiende.
     * OJO: Es vital agregar el prefijo "ROLE_" porque o si no, las anotaciones @PreAuthorize("hasRole('ADMIN')") fallan.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    // Le indicamos a Spring que nuestro "identificador principal" para el login es el email, no un username tradicional.
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // Métodos de estado de cuenta:
    // Los dejamos en 'true' (hardcoded) por ahora. 
    // Si más adelante queremos implementar "Banear usuario" o "Confirmar email", aquí pondríamos esa lógica.

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