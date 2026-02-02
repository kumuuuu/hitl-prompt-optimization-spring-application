package com.kumuditha.hitl.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() throws Exception {

        // Use try-with-resources to ensure the stream is closed
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("supabase-jwks.json")) {

            if (is == null) {
                throw new IllegalStateException("supabase-jwks.json not found");
            }

            String jwks = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JWKSet jwkSet = JWKSet.parse(jwks);

            return NimbusJwtDecoder
                    .withJwkSource(new ImmutableJWKSet<>(jwkSet))
                    .jwsAlgorithm(SignatureAlgorithm.ES256)
                    .build();
        }
    }
}
