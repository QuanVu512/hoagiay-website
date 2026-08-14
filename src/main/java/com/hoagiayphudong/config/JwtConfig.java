package com.hoagiayphudong.config;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    @Bean
    JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey(jwtProperties)));
    }

    @Bean
    JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
        return NimbusJwtDecoder.withSecretKey(getSecretKey(jwtProperties))
                .macAlgorithm(JWT_ALGORITHM)
                .build();
    }

    private SecretKey getSecretKey(JwtProperties jwtProperties) {
        if (!StringUtils.hasText(jwtProperties.base64Secret())) {
            throw new IllegalStateException("JWT_BASE64_SECRET chua duoc cau hinh.");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(jwtProperties.base64Secret());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT_BASE64_SECRET phai dung dinh dang base64.", exception);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_BASE64_SECRET phai la base64 secret toi thieu 32 bytes cho HS256.");
        }

        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
}
