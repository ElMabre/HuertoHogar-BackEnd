package cl.huertohogar.catalogo.controllers;

import cl.huertohogar.catalogo.entities.Producto;
import cl.huertohogar.catalogo.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/productos")
@RequiredArgsConstructor
// Seguridad crítica: Esta anotación protege TODOS los endpoints de esta clase. 
// Solo los usuarios que tengan el rol 'ADMIN' (validado en el filtro JWT) pueden acceder aquí.
@PreAuthorize("hasRole('ADMIN')") 
public class AdminProductoController {

    private final ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        // Validación de negocio: Antes de guardar, verificamos si el SKU ya existe para evitar duplicados.
        if (productoRepository.findBySku(producto.getSku()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El SKU ya existe");
        }
        Producto nuevoProducto = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        // Patrón estándar: Primero buscamos el recurso. Si no existe, lanzamos 404 (Not Found) inmediatamente.
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Actualización explícita: Mapeamos los datos nuevos sobre la entidad existente.
        producto.setSku(productoDetalles.getSku());
        producto.setNombre(productoDetalles.getNombre());
        producto.setPrecio(productoDetalles.getPrecio());
        producto.setCategoria(productoDetalles.getCategoria());
        producto.setStock(productoDetalles.getStock());
        producto.setDescripcion(productoDetalles.getDescripcion());
        producto.setImagen(productoDetalles.getImagen());
        producto.setOrigen(productoDetalles.getOrigen());
        producto.setUnidad(productoDetalles.getUnidad());
        
        Producto productoActualizado = productoRepository.save(producto);
        return ResponseEntity.ok(productoActualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        // Verificamos existencia antes de borrar para mantener consistencia en la respuesta (404 si ya no está).
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));


        productoRepository.delete(producto);

        // Retornamos 204 No Content, que es el código HTTP correcto para un borrado exitoso donde no devolvemos cuerpo.
        return ResponseEntity.noContent().build();
    }
}