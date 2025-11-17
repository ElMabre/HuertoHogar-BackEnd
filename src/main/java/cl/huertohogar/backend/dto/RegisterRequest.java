package cl.huertohogar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String region;
    private String comuna;
    private String direccion;
    // No pedimos el ROL, por defecto será "CLIENTE"
}