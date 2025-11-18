package cl.huertohogar.usuarios.repositories;

import cl.huertohogar.usuarios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Extiende JpaRepository para obtener métodos CRUD básicos.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Método personalizado para buscar un usuario por su email.
     * Spring Data JPA implementará este método automáticamente.
     * Lo usaremos en Spring Security para autenticar al usuario.
     *
     * @param email El email del usuario a buscar.
     * @return Un Optional que puede contener al Usuario si se encuentra.
     */
    Optional<Usuario> findByEmail(String email);
}