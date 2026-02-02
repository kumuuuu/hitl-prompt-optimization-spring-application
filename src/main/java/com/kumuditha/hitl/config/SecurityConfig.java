package com.kumuditha.hitl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder   // 🔑 inject YOUR decoder
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ ENABLE resource server with YOUR decoder
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.decoder(jwtDecoder))
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/debug").authenticated()
                        .requestMatchers("/api/me").authenticated()
                        .requestMatchers("/api/messages").authenticated()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
