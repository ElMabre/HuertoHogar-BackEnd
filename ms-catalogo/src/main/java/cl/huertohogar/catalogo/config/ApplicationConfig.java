package cl.huertohogar.catalogo.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    // Este archivo se deja intencionalmente vacío (o con beans generales si los necesitaras a futuro).
    
    // EXPLICACIÓN:
    // 1. No necesitamos 'UsuarioRepository' porque este servicio no consulta la tabla de usuarios.
    // 2. No necesitamos 'AuthenticationManager' ni 'PasswordEncoder' porque el Login se hace en ms-usuarios.
    // 3. La validación de seguridad se hace 100% en 'JwtAuthenticationFilter' verificando la firma del token.

}