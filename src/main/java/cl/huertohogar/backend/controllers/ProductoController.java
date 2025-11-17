package cl.huertohogar.backend.controllers;

import cl.huertohogar.backend.entities.Producto;
import cl.huertohogar.backend.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoRepository productoRepository;

    /**
     * Endpoint público para obtener todos los productos.
     * @return Lista de todos los productos en la base de datos.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint público para obtener un producto por su SKU (ID de negocio).
     * Nota: Tu frontend busca por SKU (ej. FR001), no por ID numérico (ej. 1, 2, 3).
     * Por eso usamos findBySku.
     *
     * @param sku El SKU del producto (ej. "FR001").
     * @return El producto encontrado o un error 404 si no existe.
     */
    @GetMapping("/{sku}")
    public ResponseEntity<Producto> getProductoBySku(@PathVariable String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con SKU: " + sku));
        return ResponseEntity.ok(producto);
    }
}