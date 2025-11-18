package cl.huertohogar.usuarios.dto;

import cl.huertohogar.usuarios.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Usuario usuario; // Enviamos los datos del usuario también
}