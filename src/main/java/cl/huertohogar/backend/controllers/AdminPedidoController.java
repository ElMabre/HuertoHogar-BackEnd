package cl.huertohogar.backend.controllers;

import cl.huertohogar.backend.entities.Pedido;
import cl.huertohogar.backend.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/pedidos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Toda la clase requiere ROL ADMIN
public class AdminPedidoController {

    private final PedidoRepository pedidoRepository;

    /**
     * Endpoint ADMIN para obtener todos los pedidos.
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        // Usamos EAGER fetch en la entidad Pedido, así que esto traerá los detalles
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    /**
     * Endpoint ADMIN para obtener un pedido por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        return ResponseEntity.ok(pedido);
    }

    /**
     * Endpoint ADMIN para actualizar el estado de un pedido.
     * Recibimos un JSON simple, ej: { "estado": "En camino" }
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> updatePedidoEstado(
            @PathVariable Long id, 
            @RequestBody Map<String, String> estadoUpdate) {
        
        String nuevoEstado = estadoUpdate.get("estado");
        if (nuevoEstado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'estado' es requerido");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        return ResponseEntity.ok(pedidoActualizado);
    }
}