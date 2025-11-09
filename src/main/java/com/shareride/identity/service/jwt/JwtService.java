package com.shareride.identity.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.shareride.identity.config.properties.JwtProperties;
import com.shareride.identity.entity.OAuthClient;
import com.shareride.identity.entity.Role;
import com.shareride.identity.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

import static com.shareride.identity.utils.Constants.CLIENT_ID;
import static com.shareride.identity.utils.Constants.EMAIL;
import static com.shareride.identity.utils.Constants.ROLES;
import static com.shareride.identity.utils.Constants.SERVICE;
import static com.shareride.identity.utils.Constants.TYPE;
import static com.shareride.identity.utils.Constants.USER;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateUserToken(UserDetails userDetails) {
        User user = (User) userDetails;
        UUID userId = user.getId();
        String email = user.getEmail();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getUserToken().getExpirationInMillis());

        Map<String, Object> claims = new HashMap<>();

        // Add roles as a claim
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put(ROLES, roles);
        claims.put(EMAIL, email);
        claims.put(TYPE, USER);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getUserSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateServiceToken(OAuthClient oAuthClient) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getServiceToken().getExpirationInMillis());

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLIENT_ID, oAuthClient.getClientId());
        claims.put(TYPE, SERVICE);

        // Add roles as claim
        List<String> roles = oAuthClient.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        claims.put(ROLES, roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(oAuthClient.getClientId()) // subject is the clientId
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getServiceSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateTokenAndReturnClaims(String token) {

        DecodedJWT decodedJwt = JWT.decode(token);
        String type = decodedJwt.getClaim(TYPE).asString();

        if (type == null) {
            throw new JwtException("Missing token type");
        }

        SecretKey signKey = switch (type) {
            case USER -> getUserSignKey();
            case SERVICE -> getServiceSignKey();
            default -> throw new JwtException("Unknown token type: " + type);
        };

        return Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
                .getBody(); // Will throw exception if invalid
    }

    public String extractSubject(String token) {
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
        DecodedJWT decodedJwt = JWT.decode(token);
        String type = decodedJwt.getClaim(TYPE).asString();

        SecretKey key = switch (type) {
            case USER -> getUserSignKey();
            case SERVICE -> getServiceSignKey();
            default -> throw new JwtException("Invalid token type");
        };

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private SecretKey getUserSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getUserToken().getSecretKey()));
    }

    private SecretKey getServiceSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getServiceToken().getSecretKey()));
    }
}
