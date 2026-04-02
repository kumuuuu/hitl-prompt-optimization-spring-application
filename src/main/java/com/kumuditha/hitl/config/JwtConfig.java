package com.kumuditha.hitl.config;

/*
 * File: JwtConfig.java
 *
 * Description:
 * JWT decoder configuration backed by Supabase JWKS.
 *
 * Responsibilities:
 * - Loads the JWKS document bundled with the application.
 * - Builds a JwtDecoder that validates incoming access tokens.
 *
 * Used in:
 * - SecurityConfig to configure Spring Security as an OAuth2 resource server.
 */

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

    /**
     * Creates the primary {@link JwtDecoder} used by the API.
     *
     * <p>
     * Notes:
     * </p>
     * <ul>
     * <li>The JWKS is read from the classpath resource
     * {@code supabase-jwks.json}.</li>
     * <li>The decoder is configured for ES256 to match Supabase token signing.</li>
     * </ul>
     *
     * @return a Nimbus-based JWT decoder
     * @throws Exception if the JWKS cannot be loaded or parsed
     */
    @Bean
    @Primary
    public JwtDecoder jwtDecoder() throws Exception {

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
