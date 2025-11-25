package cl.huertohogar.pedido.config;

import cl.huertohogar.pedido.services.JwtService;
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

        // Verificación inicial: Si no hay token, dejamos pasar la petición. 
        // Si el endpoint necesita protección, SecurityConfiguration lo rebotará más adelante.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        
        // 1. Validamos el token (Validación Stateless).
        // Importante: Aquí solo verificamos la firma criptográfica. No vamos a la base de datos.
        // Esto hace que el microservicio de pedidos sea muy rápido al autenticar.
        DecodedJWT decodedJWT = jwtService.validateToken(jwt);

        if (decodedJWT != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 2. Extraemos la identidad desde el token
            String userEmail = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("rol").asString(); 

            // 3. Adaptación de Rol para Spring Security
            // Recuerden: Spring requiere el prefijo "ROLE_" para que funcionen los @PreAuthorize.
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            // 4. Autenticación en Memoria
            // Construimos la sesión temporal válida solo para este request.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userEmail,
                    null,
                    List.of(authority)
            );
            
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // Paso Final: Inyectamos la autenticación en el contexto. 
            // A partir de esta línea, para Spring el usuario está "logueado".
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}