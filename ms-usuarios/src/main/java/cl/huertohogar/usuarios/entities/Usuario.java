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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Constraints a nivel de base de datos: Garantizamos integridad para que no existan duplicados de Email ni RUN.
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "run")
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

    //  Aquí se guarda el Hash (BCrypt), nunca la contraseña plana.
    // Faltaría agregar @JsonIgnore para evitar fugar este hash en las respuestas JSON.
    @Column(nullable = false)
    private String password; 

    private String region;
    private String comuna;
    private String direccion;

    // Guardamos el rol como String ("ADMIN", "CLIENTE") para que sea legible en la BD.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Adaptador: Transformamos nuestro Enum en la "Authority" que Spring entiende.
        // El prefijo "ROLE_" es obligatorio para que funcionen las anotaciones @PreAuthorize("hasRole('...')")
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        // Le indicamos a Spring que para nosotros el "username" (identificador de login) es el EMAIL.
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // Métodos de estado de cuenta:
    // Los mantenemos en 'true' por ahora. Si a futuro implementamos bloqueo de usuarios o confirmación de email, 
    // aquí es donde pondríamos esa lógica.

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