package app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la generación, extracción y validación de tokens JWT para autenticación.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Service
public class JwtService {

    /** Clave secreta JWT leída desde application.properties (application.security.jwt.secret-key). */
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // 1. Método para generar el token (solo pasándole el usuario)
    public String generarToken(UserDetails userDetails) {
        return generarToken(new HashMap<>(), userDetails);
    }

    // 2. Método interno que construye el token con la librería Jjwt
    public String generarToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // Usamos el username que definimos en getUsername() de la entidad User
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // El token durará 24 horas
                .signWith(getSignInKey())
                .compact();
    }

    // 3. Método para leer a quién pertenece el token (extrae el username)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 4. Método para comprobar si el token es válido y no ha caducado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- MÉTODOS AUXILIARES ---

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Procesa la operación correspondiente para extractExpiration.
     *
     * @param token Parámetro de entrada para la operación.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Procesa la operación correspondiente para extractClaim.
     *
     * @param token Parámetro de entrada para la operación.
     * @param claimsResolver Parámetro de entrada para la operación.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Procesa la operación correspondiente para extractAllClaims.
     *
     * @param token Parámetro de entrada para la operación.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Transforma la clave secreta (Base64) en un objeto SecretKey que entiende la librería JJWT. */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}