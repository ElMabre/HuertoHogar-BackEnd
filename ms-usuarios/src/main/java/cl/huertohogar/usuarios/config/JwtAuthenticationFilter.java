package cl.huertohogar.usuarios.config;

import cl.huertohogar.usuarios.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor 
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    // Inyectamos el servicio que conecta con la BD de usuarios.
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // Validamos la firma del token y obtenemos el email.
        final String userEmail = jwtService.validateTokenAndGetEmail(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // DIFERENCIA CLAVE CON OTROS MÓDULOS:
            // Aquí SÍ vamos a la base de datos (loadUserByUsername) por cada petición.
            // Al ser el microservicio de "Usuarios", necesitamos la información más fresca posible
            // (ej: si el usuario fue bloqueado hace 1 segundo, aquí lo detectamos inmediatamente).
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Creamos el objeto de autenticación usando los datos reales de la BD (userDetails).
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // No necesitamos la contraseña aquí, ya están autenticados por token.
                    userDetails.getAuthorities()
            );
            
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}