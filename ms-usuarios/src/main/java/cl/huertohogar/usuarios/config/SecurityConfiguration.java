package cl.huertohogar.usuarios.config;

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
@EnableMethodSecurity 
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF
            .csrf(csrf -> csrf.disable())

            // 2. Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                    // RUTAS PÚBLICAS (Auth)
                    .requestMatchers("/api/auth/**").permitAll()

                    // RUTAS PÚBLICAS (Swagger / Documentación)
                    .requestMatchers("/doc/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() 

                    // Esta ruta era del monolito para ver productos, en este microservicio de usuarios 
                    // técnicamente no hace falta, pero no daña dejarla por ahora.
                    .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll() 

                    // CUALQUIER OTRA RUTA (requiere autenticación)
                    .anyRequest().authenticated()
            )

            // 4. Gestión de sesiones (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 5. Proveedor de autenticación
            .authenticationProvider(authenticationProvider)

            // 6. Filtro JWT
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://18.211.31.168", "http://localhost"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}
