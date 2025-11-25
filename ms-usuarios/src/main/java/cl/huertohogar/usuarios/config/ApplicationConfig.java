package cl.huertohogar.usuarios.config;

import cl.huertohogar.usuarios.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    // Bean del AuthenticationManager:
    // Este es el componente principal que inyectaremos en nuestro AuthService para validar credenciales.
    // Es el que tiene el método .authenticate(username, password).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean del AuthenticationProvider:
    // Aquí es donde unimos las piezas. Le decimos a Spring:
    // 1. "Usa este servicio para buscar los datos del usuario en la BD" (UserDetailsService).
    // 2. "Usa este algoritmo para verificar que la contraseña coincida" (PasswordEncoder).
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Bean del PasswordEncoder:
    // Usamos BCrypt, que es el estándar actual.
    // Esto asegura que las contraseñas se guarden como hash en la BD y no en texto plano.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean del UserDetailsService:
    // Spring Security no sabe qué es una tabla "usuarios" ni qué es un "email".
    // Con esta expresión lambda, actuamos de puente: convertimos el "username" que pide Spring en una búsqueda por email en nuestro repositorio.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}