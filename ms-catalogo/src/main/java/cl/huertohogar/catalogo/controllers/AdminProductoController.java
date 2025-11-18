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
@PreAuthorize("hasRole('ADMIN')") // ¡IMPORTANTE! Toda la clase requiere ROL ADMIN
public class AdminProductoController {

    private final ProductoRepository productoRepository;

    /**
     * Endpoint ADMIN para obtener todos los productos (similar al público, pero protegido).
     */
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    /**
     * Endpoint ADMIN para crear un nuevo producto.
     */
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        // Validación simple para evitar SKU duplicado
        if (productoRepository.findBySku(producto.getSku()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El SKU ya existe");
        }
        Producto nuevoProducto = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    /**
     * Endpoint ADMIN para actualizar un producto existente por su ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        // 1. Buscar el producto
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // 2. Actualizar los campos
        producto.setSku(productoDetalles.getSku());
        producto.setNombre(productoDetalles.getNombre());
        producto.setPrecio(productoDetalles.getPrecio());
        producto.setCategoria(productoDetalles.getCategoria());
        producto.setStock(productoDetalles.getStock());
        producto.setDescripcion(productoDetalles.getDescripcion());
        producto.setImagen(productoDetalles.getImagen());
        producto.setOrigen(productoDetalles.getOrigen());
        producto.setUnidad(productoDetalles.getUnidad());

        // 3. Guardar los cambios
        Producto productoActualizado = productoRepository.save(producto);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Endpoint ADMIN para eliminar un producto por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        // 1. Buscar el producto
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // 2. Borrar el producto
        productoRepository.delete(producto);

        // 3. Devolver respuesta OK sin contenido (204 No Content)
        return ResponseEntity.noContent().build();
    }
}