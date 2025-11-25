package cl.huertohogar.catalogo.dto;

import cl.huertohogar.catalogo.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lombok al rescate: @Data, @Builder, etc. nos evitan escribir getters, setters y constructores manuales.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    // Este es el string del JWT que el frontend debe guardar (en LocalStorage o Cookies) para autenticar las siguientes peticiones.
    private String token;
    
    // Enviamos el objeto usuario completo aquí mismo.
    // Esto permite que el frontend actualice la interfaz (ej: mostrar nombre y rol en el navbar)
    // inmediatamente después del login, sin tener que hacer una llamada extra a la API.
    private Usuario usuario; 
}