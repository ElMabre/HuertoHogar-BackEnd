package cl.huertohogar.pedido.services;

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

    public DecodedJWT validateToken(String token) {
        try {
            // DEBUG: Imprimir la clave que está usando este servicio para ver si coincide con la de usuarios
            // (Borrar esto en producción real, pero vital para depurar ahora)
            // System.out.println("DEBUG PEDIDOS - Clave usada: " + secretKey); 

            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("huertohogar")
                    .build();
            
            return verifier.verify(token);
        } catch (Exception exception) {
            // AQUÍ ESTÁ LA CLAVE: Imprimimos el error exacto en los logs
            System.err.println("ERROR VALIDANDO TOKEN EN MS-PEDIDOS: " + exception.getMessage());
            // exception.printStackTrace(); // Descomenta si necesitas ver la traza completa
            return null; 
        }
    }
}
