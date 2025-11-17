package cl.huertohogar.backend.services;

import cl.huertohogar.backend.entities.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    // Inyectamos el valor de la clave secreta desde application.properties
    @Value("${jwt.secret.key}")
    private String secretKey;

    /**
     * Genera un token JWT para un usuario.
     */
    public String generateToken(Usuario usuario) {
        try {
            // Usamos el algoritmo HMAC256 con nuestra clave secreta
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            // Creamos el token
            return JWT.create()
                    .withIssuer("huertohogar") // Emisor
                    .withSubject(usuario.getEmail()) // El "dueño" del token (usamos el email)
                    .withClaim("rol", usuario.getRol().name()) // Añadimos el ROL como un "claim"
                    .withIssuedAt(Date.from(Instant.now())) // Fecha de emisión
                    .withExpiresAt(Date.from(Instant.now().plus(1, ChronoUnit.HOURS))) // Expira en 1 hora
                    .sign(algorithm); // Firmamos el token
        } catch (JWTCreationException exception){
            // Manejo de error
            throw new RuntimeException("Error al generar el token JWT", exception);
        }
    }

    /**
     * Valida un token y extrae el "Subject" (email del usuario).
     */
    public String validateTokenAndGetEmail(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("huertohogar")
                    .build();

            // Decodifica el token
            DecodedJWT decodedJWT = verifier.verify(token);

            // Retorna el email (subject)
            return decodedJWT.getSubject();

        } catch (JWTVerificationException exception){
            // Si el token es inválido (expirado, firma incorrecta, etc.)
            return null;
        }
    }
}