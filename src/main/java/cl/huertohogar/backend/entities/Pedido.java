package cl.huertohogar.backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa la cabecera de un pedido (tabla 'pedidos').
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: Muchos pedidos pueden pertenecer a un usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String estado; // "Pendiente", "En camino", "Completado", "Cancelado"

    @Column(nullable = false)
    private String metodoPago;

    // Relación: Un pedido tiene muchos detalles (líneas de producto)
    // "mappedBy" indica que la entidad 'DetallePedido' gestiona la relación en su campo 'pedido'.
    // "cascade = CascadeType.ALL" significa que si borramos un Pedido, se borran sus Detalles asociados.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // Evita problemas de bucles infinitos al convertir a JSON
    private List<DetallePedido> detalles = new ArrayList<>();
}