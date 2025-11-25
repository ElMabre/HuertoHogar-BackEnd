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

    // DTOs internos: Usamos estas clases estáticas solo para recibir la estructura JSON exacta que envía el carrito de compras.
    // Es más limpio que crear archivos separados si solo se usan aquí.
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
        // 1. SEGURIDAD: Identificamos al usuario desde el Token, NO desde el JSON del body.
        // Esto evita que un usuario malintencionado haga un pedido a nombre de otro enviando un ID falso.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // El "subject" del token es el email
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. Cabecera del Pedido: Creamos el objeto principal.
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente"); // Estado inicial por defecto.
        pedido.setMetodoPago("Transferencia"); 
        pedido.setTotal(request.getTotal());

        // 3. Lógica de Negocio: Procesamos cada ítem del carrito.
        // Aquí hacemos dos cosas: Crear el detalle del pedido Y descontar el stock del inventario.
        List<DetallePedido> detalles = request.getProductos().stream().map(item -> {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado ID: " + item.getProductoId()));

            // Validación crítica: No vender lo que no tenemos.
            if (producto.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para: " + producto.getNombre());
            }
            
            // Actualización de Inventario: Restamos el stock inmediatamente.
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto); // Guardamos el nuevo stock del producto.

            // Creamos la línea de detalle
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido); // Vinculamos al padre
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());
            return detalle;
        }).collect(Collectors.toList());

        pedido.setDetalles(detalles);

        // 4. Persistencia en Cascada: Al guardar el 'pedido' (padre), JPA guardará automáticamente todos los 'detalles' (hijos).
        // Esto funciona gracias a la configuración @OneToMany(cascade = CascadeType.ALL) en la entidad Pedido.
        Pedido nuevoPedido = pedidoRepository.save(pedido);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }
}