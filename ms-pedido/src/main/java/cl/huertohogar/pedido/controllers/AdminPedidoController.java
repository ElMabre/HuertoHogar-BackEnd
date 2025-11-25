package cl.huertohogar.pedido.controllers;

import cl.huertohogar.pedido.entities.Pedido;
import cl.huertohogar.pedido.repositories.PedidoRepository;
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
// Seguridad de alto nivel: Al poner esto aquí, blindamos todos los métodos de la clase.
// Solo los usuarios con JWT válido y claim "rol": "ADMIN" pueden entrar.
@PreAuthorize("hasRole('ADMIN')") 
public class AdminPedidoController {

    private final PedidoRepository pedidoRepository;

    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        // Estamos confiando en que la entidad Pedido tiene sus relaciones (Detalles) configuradas como EAGER.
        // Esto trae toda la info de una sola vez. Si la base de datos crece mucho, esto podría ponerse lento y habría que paginar.
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        return ResponseEntity.ok(pedido);
    }

    // Usamos PATCH en lugar de PUT porque solo vamos a modificar un atributo específico (el estado),
    // no vamos a reemplazar el objeto Pedido completo.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> updatePedidoEstado(
            @PathVariable Long id, 
            //  Usamos un Map<String, String> para capturar el JSON { "estado": "..." }
            // Esto nos evita crear una clase DTO (ej. UpdateStatusRequest) solo para recibir un campo.
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