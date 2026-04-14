package com.orio.book_processing.auth.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.orio.book_processing.auth.models.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.expiration-ms}")
    private Long expirationMs;

    public TokenPayload generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMs);

        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("book-processing").issuedAt(now).expiresAt(expiresAt)
                .subject(user.getEmail()).claim("uid", user.getId()).claim("role", "ROLE_USER").build();

        JwsHeader header = JwsHeader.with(() -> "HS256").build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new TokenPayload(token, expiresAt);
    }

    public record TokenPayload(String accessToken, Instant expiresAt) {
    }
}
