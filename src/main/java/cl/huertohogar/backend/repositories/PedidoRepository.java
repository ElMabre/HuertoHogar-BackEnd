package cl.huertohogar.backend.repositories;

import cl.huertohogar.backend.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Pedido.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Por ahora no necesitamos métodos personalizados,
    // JpaRepository nos da findAll(), findById(), save(), deleteById(), etc.
}