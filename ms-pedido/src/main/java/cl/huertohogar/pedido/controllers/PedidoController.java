package cl.huertohogar.pedido.controllers;

import cl.huertohogar.pedido.entities.DetallePedido;
import cl.huertohogar.pedido.entities.Pedido;
import cl.huertohogar.pedido.entities.Producto;
import cl.huertohogar.pedido.entities.Usuario;
import cl.huertohogar.pedido.repositories.PedidoRepository;
import cl.huertohogar.pedido.repositories.ProductoRepository;
import cl.huertohogar.pedido.repositories.UsuarioRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Data
    public static class PedidoRequest {
        private Double total;
        private List<DetalleRequest> productos;
    }

    @Data
    public static class DetalleRequest {
        private Long productoId;
        private Integer cantidad;
        private Double precio;
    }

    // --- CREAR PEDIDO (Ya lo tenías) ---
    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody PedidoRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente");
        pedido.setMetodoPago("Transferencia"); 
        pedido.setTotal(request.getTotal());

        List<DetallePedido> detalles = request.getProductos().stream().map(item -> {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado ID: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para: " + producto.getNombre());
            }
            
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());
            return detalle;
        }).collect(Collectors.toList());

        pedido.setDetalles(detalles);
        Pedido nuevoPedido = pedidoRepository.save(pedido);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<Pedido>> getMyPedidos() {
        // 1. Identificamos al usuario por su token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. Buscamos solo sus pedidos
        return ResponseEntity.ok(pedidoRepository.findByUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPedido(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        // 1. Seguridad: Validar que el pedido sea del usuario que intenta borrarlo
        if (!pedido.getUsuario().getEmail().equals(email)) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este pedido");
        }

        // 2. Regla de Negocio: Solo se pueden cancelar pedidos "Pendientes"
        if (!"Pendiente".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cancelar un pedido que ya fue procesado");
        }

        // 3. Devolución de Stock (Opcional pero recomendado)
        // Recorremos los detalles para devolver los productos al inventario
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
        
        // 4. Borramos
        pedidoRepository.delete(pedido);
        return ResponseEntity.noContent().build();
    }
}