package cl.huertohogar.catalogo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF porque estamos trabajando con una API REST Stateless (sin sesiones de servidor), así evitamos bloqueos innecesarios.
            .csrf(csrf -> csrf.disable())
            // Conectamos nuestra configuración de CORS definida más abajo para permitir peticiones desde el front.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Dejamos abiertos los endpoints de documentación (Swagger) para facilitar las pruebas sin necesidad de token.
                .requestMatchers("/doc/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Ojo aquí: Permitimos que cualquiera vea los productos (GET), pero cualquier otra acción (POST, DELETE) requerirá autenticación.
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .anyRequest().authenticated()
            )
            // Fundamental: Al usar JWT, la política de creación de sesión debe ser STATELESS. No guardamos nada en memoria del servidor.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Insertamos nuestro filtro JWT antes del filtro estándar de Spring para validar el token al principio de la cadena.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Aquí definimos los orígenes permitidos: localhost para desarrollo y la IP del servidor en producción (EC2).
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://18.211.31.168", "http://localhost"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}