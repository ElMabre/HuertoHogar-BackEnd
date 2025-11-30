package cl.huertohogar.pedido.repositories;

import cl.huertohogar.pedido.entities.Pedido;
import cl.huertohogar.pedido.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuario(Usuario usuario);
}