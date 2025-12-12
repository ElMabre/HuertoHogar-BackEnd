package cl.huertohogar.pedido.controllers;

import cl.huertohogar.pedido.entities.DetallePedido;
import cl.huertohogar.pedido.entities.Pedido;
import cl.huertohogar.pedido.entities.Producto;
import cl.huertohogar.pedido.entities.Usuario;
import cl.huertohogar.pedido.repositories.PedidoRepository;
import cl.huertohogar.pedido.repositories.ProductoRepository;
import cl.huertohogar.pedido.repositories.UsuarioRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Value("${mercadopago.access.token}")
    private String mpAccessToken;

    @Value("${mercadopago.back.url.success}")
    private String backUrlSuccess;

    @Value("${mercadopago.back.url.failure}")
    private String backUrlFailure;

    @Value("${mercadopago.back.url.pending}")
    private String backUrlPending;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(mpAccessToken);
    }

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

    // --- CREAR PEDIDO CON MERCADO PAGO ---
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPedido(@RequestBody PedidoRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente");
        pedido.setMetodoPago("MercadoPago"); 
        pedido.setTotal(request.getTotal());

        List<DetallePedido> detalles = new ArrayList<>();
        List<PreferenceItemRequest> mpItems = new ArrayList<>();

        // Procesar productos y preparar items para Mercado Pago
        for (DetalleRequest item : request.getProductos()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado ID: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para: " + producto.getNombre());
            }
            
            // Actualizar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            // Crear detalle
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());
            detalles.add(detalle);

            // Crear item para Mercado Pago
            PreferenceItemRequest mpItem = PreferenceItemRequest.builder()
                    .id(String.valueOf(producto.getId()))
                    .title(producto.getNombre())
                    .description("Compra en Huerto Hogar")
                    .pictureUrl(producto.getImagen()) 
                    .categoryId("home")
                    .quantity(item.getCantidad())
                    .currencyId("CLP")
                    .unitPrice(new BigDecimal(item.getPrecio()))
                    .build();
            mpItems.add(mpItem);
        }

        pedido.setDetalles(detalles);
        Pedido nuevoPedido = pedidoRepository.save(pedido);
        
        // --- INTEGRACIÓN MERCADO PAGO ---
        String paymentUrl = null;
        try {
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(backUrlSuccess)
                    .failure(backUrlFailure)
                    .pending(backUrlPending)
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(mpItems)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .externalReference(String.valueOf(nuevoPedido.getId())) // Referencia para conciliar después
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            paymentUrl = preference.getInitPoint(); // URL para redirigir al usuario

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al comunicar con Mercado Pago: " + e.getMessage());
        }

        // Respuesta combinada
        Map<String, Object> response = new HashMap<>();
        response.put("pedido", nuevoPedido);
        response.put("paymentUrl", paymentUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<Pedido>> getMyPedidos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return ResponseEntity.ok(pedidoRepository.findByUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPedido(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        if (!pedido.getUsuario().getEmail().equals(email)) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este pedido");
        }

        if (!"Pendiente".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cancelar un pedido que ya fue procesado");
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
        
        pedidoRepository.delete(pedido);
        return ResponseEntity.noContent().build();
    }
}