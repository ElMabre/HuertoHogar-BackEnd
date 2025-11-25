package cl.huertohogar.pedido.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: Muchos detalles pertenecen a un pedido.
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    // CRÍTICO: @JsonBackReference le dice a Jackson que NO serialice esta parte de la relación.
    // Esto evita el error de "StackOverflow" por bucle infinito (Pedido -> Detalle -> Pedido -> Detalle...) al enviar el JSON.
    @JsonBackReference 
    private Pedido pedido;

    // Relación: Muchos detalles pueden apuntar al mismo producto.
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    // Lógica de Negocio: Guardamos el precio AQUÍ y no confiamos en el precio del producto.
    // Razón: Si mañana cambiamos el precio del tomate en el catálogo, el historial de este pedido antiguo NO debe cambiar.
    @Column(nullable = false)
    private Double precioUnitario; 
}