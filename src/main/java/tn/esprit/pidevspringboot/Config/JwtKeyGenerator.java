package tn.esprit.pidevspringboot.Config;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.security.SecureRandom;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("🔑 Nouvelle clé secrète (Base64) : " + encodedKey);
    }
}
