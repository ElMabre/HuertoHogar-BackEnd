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

    // DTOs internos para recibir el JSON del carrito
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

    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody PedidoRequest request) {
        // 1. Obtener el usuario autenticado (desde el Token JWT)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // El "subject" del token es el email
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. Crear la cabecera del Pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente");
        pedido.setMetodoPago("Transferencia"); // Podrías recibir esto del front también
        pedido.setTotal(request.getTotal());

        // 3. Procesar los productos (Detalles)
        List<DetallePedido> detalles = request.getProductos().stream().map(item -> {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado ID: " + item.getProductoId()));

            // (Opcional) Validar Stock
            if (producto.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para: " + producto.getNombre());
            }
            // Descontar stock
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

        // 4. Guardar todo en cascada
        Pedido nuevoPedido = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }
}