package com.hoagiayphudong.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.hoagiayphudong.config.JwtConfig;
import com.hoagiayphudong.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String ISSUER = "hoagiayphudong";

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public AccessTokenResult createAccessToken(Authentication authentication, Long userId) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtProperties.validityInSeconds());
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String scope = roles.stream().collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                .claim("id", String.valueOf(userId))
                .claim("roles", roles)
                .claim("scope", scope)
                .build();
        JwsHeader header = JwsHeader.with(JwtConfig.JWT_ALGORITHM).build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AccessTokenResult(
                "Bearer",
                accessToken,
                jwtProperties.validityInSeconds(),
                expiresAt,
                userId,
                authentication.getName(),
                roles
        );
    }

    public record AccessTokenResult(
            String tokenType,
            String accessToken,
            long expiresIn,
            Instant expiresAt,
            Long userId,
            String username,
            List<String> roles
    ) {
    }
}
