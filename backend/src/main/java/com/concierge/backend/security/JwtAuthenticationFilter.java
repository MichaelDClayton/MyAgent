package com.concierge.backend.security;

import com.concierge.backend.context.TenantContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            JwtProvider jwtProvider = new JwtProvider();
            String token = authHeader.substring(7);
            Claims claims = Jwts.parser().verifyWith(jwtProvider.getSigningKey()).build().parseSignedClaims(token).getPayload();

            String tenantId = claims.get("tenantId", String.class);
            // Set tenantId for Postgres RLS context
            TenantContext.setCurrentTenant(tenantId);

            // Extract roles from JWT claims
            List<String> roles = claims.get("roles", List.class);

            // Map strings to Spring Security GrantedAuthority objects
            List<SimpleGrantedAuthority> authorities = roles == null ? List.of() : roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // Standard Spring Security Authentication
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
