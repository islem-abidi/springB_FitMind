package tn.esprit.pidevspringboot.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.User.Token;
import tn.esprit.pidevspringboot.Repository.TokenRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretkey;

    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getRoleType().name());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        Token newToken = Token.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtExpiration / 1000))
                .isValid(true)
                .user(user)
                .build();
        now = new Date();
        expiryDate = new Date(now.getTime() + jwtExpiration);

        tokenRepository.save(newToken);
        return token;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void revokeToken(String token) {
        tokenRepository.findByToken(token).ifPresent(tokenRepository::delete);
    }
    public void revokeAllTokensForUser(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getIdUser());
        for (Token t : validTokens) {
            t.setRevoked(true);
            t.setExpired(true);
        }
        tokenRepository.saveAll(validTokens);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
