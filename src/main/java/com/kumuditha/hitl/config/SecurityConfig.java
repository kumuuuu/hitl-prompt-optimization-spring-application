package com.kumuditha.hitl.config;

/*
 * File: SecurityConfig.java
 *
 * Description:
 * Spring Security configuration for the HTTP API.
 *
 * Responsibilities:
 * - Enforces stateless, token-based authentication.
 * - Configures the application as an OAuth2 resource server using JWT.
 * - Defines which endpoints require authentication.
 *
 * Used in:
 * - Spring Security filter chain during request processing.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        /**
         * Builds the {@link SecurityFilterChain} for the API.
         *
         * <p>
         * Key decisions:
         * </p>
         * <ul>
         * <li>Stateless sessions: each request must carry its own credentials.</li>
         * <li>JWT validation: delegated to the provided {@link JwtDecoder}.</li>
         * </ul>
         *
         * @param http       Spring Security HTTP builder
         * @param jwtDecoder decoder used to validate inbound JWTs
         * @return the configured filter chain
         * @throws Exception if the security chain cannot be built
         */
        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        JwtDecoder jwtDecoder) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> {
                                })
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/debug").authenticated()
                                                .requestMatchers("/api/me").authenticated()
                                                .requestMatchers("/api/messages").authenticated()
                                                .anyRequest().permitAll());

                return http.build();
        }
}
