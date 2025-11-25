package cl.huertohogar.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Este objeto captura todos los datos del formulario de registro del frontend.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    
    // Datos personales básicos
    private String nombre;
    private String apellido;
    private String run;
    
    // Credenciales
    private String email;
    private String password;
    
    // Datos de ubicación (necesarios para futuros envíos)
    private String region;
    private String comuna;
    private String direccion;
    
    // NOTA DE SEGURIDAD: 
    // Intencionalmente NO incluimos el campo 'rol' en este request.
    // Esto evita vulnerabilidades de "Mass Assignment" donde un usuario malicioso podría intentar registrarse enviando "rol": "ADMIN".
    // La asignación del rol 'CLIENTE' se hará forzosamente en el AuthService.
}