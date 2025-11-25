package cl.huertohogar.catalogo.config;

import cl.huertohogar.catalogo.services.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Al extender de OncePerRequestFilter, nos aseguramos de que este filtro se ejecute una sola vez por cada petición HTTP.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Verificamos si no hay header o si no empieza con "Bearer ". 
        // Si pasa esto, dejamos continuar la petición sin autenticar (útil para endpoints públicos como el login).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Quitamos la palabra "Bearer " para quedarnos solo con el string del token.
        final String jwt = authHeader.substring(7);

        // Aquí validamos que el token sea correcto (firma, expiración) usando nuestro servicio.
        DecodedJWT decodedJWT = jwtService.validateToken(jwt);

        // Si el token es válido y el usuario aún no está autenticado en el contexto actual, procedemos a autenticarlo manualmente.
        if (decodedJWT != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String userEmail = decodedJWT.getSubject();
            
            // Extraemos el rol del token.
            String role = decodedJWT.getClaim("rol").asString(); 
            
            // Creamos la autoridad. Ojo: Spring Security suele necesitar el prefijo "ROLE_" para que funcionen los @PreAuthorize.
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            
            // Creamos el objeto de autenticación estándar de Spring.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userEmail,
                    null,
                    List.of(authority)
            );
            
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            //  inyectamos la autenticación en el contexto de seguridad para que Spring sepa que el usuario es válido durante el resto del request.
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}