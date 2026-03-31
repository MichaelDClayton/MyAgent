package com.concierge.backend.security;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretString;

    @Getter
    private SecretKey signingKey;

    @PostConstruct
    protected void init() {
        // Convert your Base64 string into a Key object
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

}
