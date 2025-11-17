package cl.huertohogar.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una línea de detalle dentro de un pedido (tabla 'detalles_pedido').
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: Muchos detalles pertenecen a un pedido
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonBackReference // Evita problemas de bucles infinitos al convertir a JSON
    private Pedido pedido;

    // Relación: Muchos detalles pueden apuntar al mismo producto
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario; // Guardamos el precio al momento de la compra
}