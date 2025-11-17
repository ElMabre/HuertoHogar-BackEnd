package cl.huertohogar.backend.repositories;

import cl.huertohogar.backend.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Producto.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Método personalizado para buscar un producto por su SKU (ID de negocio).
     */
    Optional<Producto> findBySku(String sku);
}