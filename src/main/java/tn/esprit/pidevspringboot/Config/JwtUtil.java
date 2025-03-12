package tn.esprit.pidevspringboot.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    // ✅ Clé secrète pour signer le token (remplacez par la vôtre)
    private final String secretKey = "sCc2TG0EXqQdTG86LTxrHjMKfEkzovs+RotRwOUGpUQ=";

    // ✅ Stockage des tokens pour chaque utilisateur
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Générer et stocker un Token JWT
    public String generateToken(String email) {
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expiration : 1h
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        // ✅ Stocker le token en mémoire
        tokenStore.put(email, token);
        return token;
    }

    // ✅ Récupérer un Token JWT stocké pour un utilisateur
    public String getToken(String email) {
        return tokenStore.get(email);
    }

    // ✅ Extraire l'email depuis le Token JWT
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ Vérifier si le Token est valide
    public boolean validateToken(String token, String email) {
        return (email.equals(extractEmail(token)) && !isTokenExpired(token));
    }

    // ✅ Vérifier si le Token est expiré
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // ✅ Extraire les Claims du Token JWT
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // ✅ Méthode correcte en 0.12.0
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
