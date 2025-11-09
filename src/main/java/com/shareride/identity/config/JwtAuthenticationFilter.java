package com.shareride.identity.config;

import com.shareride.identity.service.impl.UserDetailsServiceImpl;
import com.shareride.identity.service.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.shareride.identity.utils.Constants.*;
import static com.shareride.identity.utils.Constants.Security.ACTUATOR_REGEX;
import static com.shareride.identity.utils.Constants.Security.API_V1_AUTH_REGEX;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDE_PATHS = List.of(
            API_V1_AUTH_REGEX,
            ACTUATOR_REGEX
    );

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            String authHeader = request.getHeader(AUTHORIZATION);
//            String token = null;
//            String subject = null;
//
//            if (authHeader != null && authHeader.startsWith(BEARER_)) {
//                token = authHeader.substring(7);
//                if (jwtService.validateTokenAndReturnClaims(token)) {
//                    subject = jwtService.extractSubject(token);
//                }
//            }
//
//            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                List<GrantedAuthority> authorities = jwtService.extractAuthorities(token);
//                UserDetails userDetails = new User(subject, EMPTY, authorities);
//
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//
//        } catch (JwtException | IllegalArgumentException ex) {
//            request.setAttribute(JWT_EXCEPTION, ex);
//        }
//
//        filterChain.doFilter(request, response);

        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.validateTokenAndReturnClaims(token);
            if (claims == null) {
                // Invalid or expired token
                filterChain.doFilter(request, response);
                return;
            }

            String type = claims.get(TYPE, String.class);
            String subject = claims.getSubject(); // userId or clientId
            List<String> roles = claims.get(ROLES, List.class);

            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = roles != null
                        ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                        : List.of();

                // Distinguish between USER and SERVICE principals
                String principalName = switch (type) {
                    case USER -> "user:" + subject;
                    case SERVICE -> "service:" + subject;
                    default -> "unknown:" + subject;
                };

                UserDetails principal = new User(principalName, EMPTY, authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException ex) {
            request.setAttribute(JWT_EXCEPTION, ex);
        }

        filterChain.doFilter(request, response);
    }
}
