package com.kumuditha.hitl.config;

/*
 * File: CorsConfig.java
 *
 * Description:
 * Centralized CORS configuration for the HTTP API.
 *
 * Responsibilities:
 * - Defines which browser origins may call the backend.
 * - Controls allowed methods/headers and whether credentials are permitted.
 *
 * Used in:
 * - Spring MVC filter chain via the configured CorsConfigurationSource bean.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

        /**
         * Builds the CORS configuration applied to all request paths.
         *
         * <p>
         * Notes:
         * </p>
         * <ul>
         * <li>The allowed origins list is intentionally limited to known development
         * hosts.</li>
         * <li>Credentials are enabled to support auth headers/cookies in cross-origin
         * requests.</li>
         * </ul>
         *
         * @return a CorsConfigurationSource registered for all paths
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                // Known frontend origins for local development.
                config.setAllowedOrigins(List.of(
                                "http://localhost:3000",
                                "http://127.0.0.1:3000",
                                "http://localhost:5500"));

                config.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"));

                config.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type"));

                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", config);
                return source;
        }
}
