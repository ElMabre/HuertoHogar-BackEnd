package cl.huertohogar.pedido.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta entidad mapea la misma tabla "productos" que usa el Catálogo.
// En el contexto de Pedidos, la usamos principalmente para leer precios y descontar stock.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El SKU es el vínculo lógico entre lo que ve el cliente (Catálogo) y lo que procesamos aquí.
    @Column(nullable = false, unique = true)
    private String sku; 

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private String categoria;

    // DATO CRÍTICO EN ESTE MÓDULO:
    // El servicio de Pedidos es el "dueño" de la escritura de este campo.
    // Cada vez que se confirma una compra, este número debe bajar.
    @Column(nullable = false)
    private Integer stock;

    @Column(length = 1000) 
    private String descripcion;

    @Column(length = 512) 
    private String imagen;
    
    private String origen;
    
    @Column(nullable = false)
    private String unidad; 
}