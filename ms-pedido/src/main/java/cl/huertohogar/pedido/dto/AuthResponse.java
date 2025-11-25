package cl.huertohogar.pedido.dto;

import cl.huertohogar.pedido.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    // El JWT que el cliente debe guardar.
    private String token;
    
    // UX: Retornamos el usuario completo para que el frontend pueda pintar la interfaz (nombre, email) de inmediato.
    // IMPORTANTE: Asegurarse de que la entidad Usuario tenga @JsonIgnore en el password, si no, lo estaremos enviando aquí.
    private Usuario usuario; 
}