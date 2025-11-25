package cl.huertohogar.pedido.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el Cliente.
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Solo guardamos la fecha (día), no la hora exacta. 
    // Si necesitamos reportes de "horas pico" de venta, tendríamos que migrar a LocalDateTime.
    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Double total;

    // Estado del flujo de negocio.
    // Nota: Actualmente es String abierto, idealmente migrar a un Enum para evitar errores de tipeo ("Entregado" vs "entregado").
    @Column(nullable = false)
    private String estado; 

    @Column(nullable = false)
    private String metodoPago;

    // --- RELACIÓN CLAVE ---
    // 1. mappedBy = "pedido": Indica que la clase DetallePedido es la dueña de la clave foránea en la BD.
    // 2. CascadeType.ALL: Si borramos este pedido, Hibernate borrará automáticamente todos sus detalles.
    // 3. FetchType.EAGER: Significa que siempre que consultemos un pedido, la app traerá también todos sus detalles.
    //    Es cómodo para ver el resumen de compra, pero si tenemos miles de pedidos, puede impactar el rendimiento.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // 4. @JsonManagedReference: Esta es la "parte visible" de la relación en el JSON.
    //    Complementa al @JsonBackReference de DetallePedido para que la lista de productos sí aparezca en la respuesta.
    @JsonManagedReference 
    private List<DetallePedido> detalles = new ArrayList<>();
}