package com.shopflow.user_service.service;

import com.shopflow.user_service.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service responsable de tout ce qui touche aux tokens JWT :
 *   - Générer un token après login/register
 *   - Valider un token reçu dans une requête
 *   - Extraire les informations du token (email, expiration...)
 *
 * Librairie utilisée : jjwt (io.jsonwebtoken)
 */
@Service
@Slf4j  // injecte un logger : log.info(), log.error(), etc.
public class JwtService {

    // Injecté depuis application-dev.yml → jwt.secret
    @Value("${jwt.secret}")
    private String secretKey;

    // Injecté depuis application-dev.yml → jwt.expiration (86400000 = 24h en ms)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Génère un token JWT pour un utilisateur.
     * On y ajoute le rôle en "claim" — info supplémentaire dans le payload.
     *
     * Structure du payload généré :
     * {
     *   "sub": "john@example.com",    ← subject = identifiant principal
     *   "role": "USER",               ← claim personnalisé
     *   "iat": 1234567890,            ← issued at (émis le)
     *   "exp": 1234654290             ← expiration
     * }
     */
    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("firstName", user.getFirstName());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())           // l'email comme identifiant
                .issuedAt(new Date())               // date d'émission = maintenant
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())           // signe avec notre clé secrète
                .compact();                          // génère la String finale
    }

    /**
     * Extrait l'email (subject) du token.
     * Utilisé par JwtAuthFilter pour identifier l'utilisateur.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Vérifie que le token est valide :
     * 1. La signature correspond à notre clé secrète
     * 2. Le token n'est pas expiré
     * 3. Le subject correspond à l'utilisateur attendu
     */
    public boolean isTokenValid(String token, String userEmail) {
        try {
            final String email = extractEmail(token);
            return email.equals(userEmail) && !isTokenExpired(token);
        } catch (JwtException e) {
            // Token malformé, signature invalide, etc.
            log.warn("Token JWT invalide : {}", e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // Méthodes privées — détails d'implémentation
    // -------------------------------------------------------

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        // Jwts.parser() vérifie automatiquement la signature
        // Si la signature est invalide → JwtException levée
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Convertit la clé secrète String en objet SecretKey
     * compatible avec l'algorithme HMAC-SHA256.
     * La clé doit faire au moins 256 bits (32 caractères).
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secretKey.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}