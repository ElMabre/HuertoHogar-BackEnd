package cl.huertohogar.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO (Data Transfer Object) estricto para el login.
// Solo aceptamos email y password para mapear el JSON que llega en el cuerpo del POST /auth/login.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}