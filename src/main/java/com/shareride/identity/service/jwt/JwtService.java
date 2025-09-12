package com.shareride.identity.service.jwt;

import com.shareride.identity.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.shareride.identity.utils.Constants.EMAIL;
import static com.shareride.identity.utils.Constants.PropertyKeys.JWT_EXPIRATION_IN_MILLIS;
import static com.shareride.identity.utils.Constants.PropertyKeys.JWT_SECRET_KEY;
import static com.shareride.identity.utils.Constants.ROLES;

@Service
public class JwtService {

    private final String jwtSecret;
    private final long jwtExpirationTimeMs;

    public JwtService(@Value(JWT_SECRET_KEY) String jwtSecret, @Value(JWT_EXPIRATION_IN_MILLIS) long jwtExpirationTimeMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationTimeMs = jwtExpirationTimeMs;
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateToken(userPrincipal);
    }

    public String generateToken(UserDetails userDetails) {
        User user = (User) userDetails;
        UUID userId = user.getId();
        String email = user.getEmail();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationTimeMs);

        Map<String, Object> claims = new HashMap<>();

        // Add roles as a claim
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put(ROLES, roles);
        claims.put(EMAIL, email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token); // Will throw exception if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw e;
        }
    }

    public String extractUserId(String token) {
       return parseClaims(token).getSubject();
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = parseClaims(token);
        List<String> roles = claims.get(ROLES, List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
