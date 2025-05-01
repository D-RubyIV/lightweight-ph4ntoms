package com.ph4ntoms.authenticate.security.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    @Autowired
    private KeyPairProvider keyPairProvider;

    public String generateToken(String userName, Date expiration) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName, expiration);
    }

    private Algorithm getAlgorithm() {
        RSAPublicKey publicKey = keyPairProvider.getRsaPublicKey();
        RSAPrivateKey privateKey = keyPairProvider.getRsaPrivateKey();
        return Algorithm.RSA256(publicKey, privateKey);
    }

    private String createToken(Map<String, Object> claims, String userName, Date expiration) {

        return JWT.create()
                .withSubject(userName)
                .withIssuer("PHANTOM-SERVER")
                .withIssuedAt(new Date())
                .withExpiresAt(expiration)
                .withJWTId(UUID.randomUUID().toString())
                .sign(getAlgorithm());
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPairProvider.getRsaPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the token against user details and expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}