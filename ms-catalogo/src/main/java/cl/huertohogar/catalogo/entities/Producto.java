package cl.huertohogar.catalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa la tabla 'productos' en la base de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku; // Usaremos el SKU (ej. FR001) como un ID de negocio

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 1000) // Permite una descripción más larga (hasta 1000 caracteres)
    private String descripcion;

    @Column(length = 512) // Permite URLs de imagen largas
    private String imagen;
    
    private String origen;
    
    @Column(nullable = false)
    private String unidad; // ej. "por kilo", "por unidad"
}