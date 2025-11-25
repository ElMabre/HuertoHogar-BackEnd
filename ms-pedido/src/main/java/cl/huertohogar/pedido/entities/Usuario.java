package cl.huertohogar.pedido.entities;

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
// Definimos constraints para asegurar que no existan dos usuarios con el mismo email o RUN a nivel de base de datos.
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

    // IMPORTANTE: Aquí se almacena el Hash (Bcrypt).
    // NOTA DE SEGURIDAD: Faltaría agregar @JsonIgnore para que este campo nunca viaje al frontend en los JSONs.
    @Column(nullable = false)
    private String password; 

    private String region;
    private String comuna;
    private String direccion;

    // Guardamos el rol como String ("ADMIN") para facilitar la lectura directa en la base de datos.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // --- MÉTODOS DE USERDETAILS (Spring Security) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Adaptador: Convertimos nuestro Enum en una Authority de Spring.
        // El prefijo "ROLE_" es obligatorio para que funcionen las anotaciones @PreAuthorize.
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        // Sobrescribimos esto para indicarle a Spring que usamos el EMAIL como identificador de login.
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // Métodos de estado de cuenta (Account Status):
    // Están harcodeados en 'true' para esta fase. 
    // Si más adelante queremos implementar "Banear usuario" o "Confirmar email", modificamos estos métodos.

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