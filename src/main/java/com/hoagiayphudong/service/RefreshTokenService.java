package com.hoagiayphudong.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;

import com.hoagiayphudong.config.JwtProperties;
import com.hoagiayphudong.model.RefreshToken;
import com.hoagiayphudong.model.User;
import com.hoagiayphudong.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 64;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public RefreshTokenResult issueToken(User user) {
        String tokenValue = generateTokenValue();
        OffsetDateTime expiresAt = OffsetDateTime.now()
                .plusSeconds(jwtProperties.refreshTokenValidityInSeconds());

        refreshTokenRepository.save(new RefreshToken(user, hashToken(tokenValue), expiresAt));
        return new RefreshTokenResult(tokenValue, expiresAt.toInstant(), user);
    }

    @Transactional
    public RefreshTokenResult rotateToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashToken(refreshTokenValue))
                .orElseThrow(() -> new BadCredentialsException("Refresh token khong hop le."));

        if (!refreshToken.isUsable()) {
            throw new BadCredentialsException("Refresh token da het han hoac da bi thu hoi.");
        }

        User user = refreshToken.getUser();
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new DisabledException("Tai khoan da bi khoa.");
        }

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        return issueToken(user);
    }

    @Transactional
    public void revokeToken(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHash(hashToken(refreshTokenValue))
                .ifPresent(refreshToken -> {
                    if (refreshToken.getRevokedAt() == null) {
                        refreshToken.revoke();
                        refreshTokenRepository.save(refreshToken);
                    }
                });
    }

    private String generateTokenValue() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String tokenValue) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(tokenValue.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Khong the hash refresh token.", exception);
        }
    }

    public record RefreshTokenResult(
            String tokenValue,
            Instant expiresAt,
            User user
    ) {
    }
}
