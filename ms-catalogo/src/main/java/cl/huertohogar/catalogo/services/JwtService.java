package cl.huertohogar.catalogo.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    /**
     * Valida el token y devuelve el objeto DecodedJWT completo
     * para que podamos sacar el email y el ROL.
     */
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("huertohogar")
                    .build();
            
            return verifier.verify(token);
        } catch (Exception exception) {
            return null; // Token inválido
        }
    }
}