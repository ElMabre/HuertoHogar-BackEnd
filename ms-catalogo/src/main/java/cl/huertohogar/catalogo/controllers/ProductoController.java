package cl.huertohogar.catalogo.controllers;

import cl.huertohogar.catalogo.entities.Producto;
import cl.huertohogar.catalogo.repositories.ProductoRepository;
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

    // Este endpoint es público. Coincide con la excepción que configuramos en SecurityConfiguration (.permitAll).
    // Es el que usa el Home del frontend para cargar la grilla de productos.
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    // Importante: El frontend navega usando el SKU (ej: /producto/TOMATE-01) para tener URLs amigables (SEO),
    // no usa el ID numérico interno de la base de datos (Long). Por eso aquí buscamos por SKU.
    @GetMapping("/{sku}")
    public ResponseEntity<Producto> getProductoBySku(@PathVariable String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con SKU: " + sku));
        return ResponseEntity.ok(producto);
    }
}