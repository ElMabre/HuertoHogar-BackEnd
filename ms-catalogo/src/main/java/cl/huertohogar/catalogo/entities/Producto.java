package cl.huertohogar.catalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID técnico autogenerado (Primary Key).

    // Este es el ID de negocio real.
    // Importante: Tiene 'unique = true' a nivel de base de datos para asegurar integridad.
    // El frontend lo usará en las rutas (ej: /producto/TOM-01) en lugar del ID numérico.
    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Integer stock;

    // Definimos un varchar(1000) porque el estándar (255) se queda corto para descripciones detalladas de productos.
    @Column(length = 1000) 
    private String descripcion;

    // Ampliamos a 512 caracteres para no tener problemas si usamos URLs largas (ej: tokens de S3 o CDNs externos) en el futuro.
    @Column(length = 512) 
    private String imagen;
    
    private String origen;
    
    // Vital para la UI: define si mostramos el precio "$X / kilo" o "$X / unidad".
    @Column(nullable = false)
    private String unidad; 
}