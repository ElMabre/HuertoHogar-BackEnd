package cl.huertohogar.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
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
@EnableMethodSecurity // Habilita la seguridad a nivel de método (ej. @PreAuthorize)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF (Cross-Site Request Forgery)
            // Es común en APIs REST, ya que no usamos sesiones/cookies.
            .csrf(csrf -> csrf.disable())

            // 2. (IMPORTANTE) Configurar CORS
            // Permite que tu frontend de React (en localhost:3000) se comunique con este backend (en localhost:8080)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // // 3. Definir las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                    // RUTAS PÚBLICAS (no requieren autenticación)
                    .requestMatchers("/api/auth/**").permitAll() // Login y Registro
                    .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll() // <-- CORREGIDO
                    .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll() // Ver productos

                    // RUTAS DE ADMINISTRADOR (requieren ROL "ADMIN")
                    // Usaremos @PreAuthorize en los controladores para esto, es más limpio.
                    // .requestMatchers("/api/admin/**").hasRole("ADMIN")

                    // CUALQUIER OTRA RUTA (requiere autenticación)
                    .anyRequest().authenticated()
            )

            // 4. Configurar la gestión de sesiones
            // Le decimos a Spring que no cree sesiones, usaremos JWT (STATELESS)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 5. Definir el proveedor de autenticación
            .authenticationProvider(authenticationProvider)

            // 6. Añadir nuestro filtro JWT
            // Le decimos que use nuestro JwtAuthenticationFilter ANTES del filtro de login estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para la configuración de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite peticiones desde el origen de React (ajusta el puerto si es necesario)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // Métodos permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Headers permitidos
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        // Permitir credenciales (cookies, etc., aunque no las usemos mucho con JWT)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica esta configuración a todas las rutas
        return source;
    }
}