package cl.huertohogar.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mapeo directo del formulario de registro del frontend.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    
    private String nombre;
    private String apellido;
    private String run;
    private String email;
    private String password;
    
    // Datos necesarios para el envío de pedidos futuros.
    private String region;
    private String comuna;
    private String direccion;
    
    // SEGURIDAD: 
    // Intencionalmente NO recibimos el campo 'rol'.
    // Esto evita que un atacante envíe un JSON modificado {"rol": "ADMIN"} y se registre con privilegios elevados.
    // La asignación del rol se hará en el backend (AuthService) por defecto a "CLIENTE".
}