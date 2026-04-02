package com.kumuditha.hitl.service;

/*
 * File: AmbiguityAnalysisService.java
 *
 * Description:
 * Client for the ambiguity detection microservice.
 *
 * Responsibilities:
 * - Sends raw user text to the ML service.
 * - Deserializes and returns the ambiguity analysis response.
 * - Logs a safe JSON representation for diagnostics.
 *
 * Used in:
 * - MessageService to enrich prompts with ambiguity context.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuditha.hitl.dto.ml.AmbiguityRequest;
import com.kumuditha.hitl.dto.ml.AmbiguityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AmbiguityAnalysisService {

        private static final Logger log = LoggerFactory.getLogger(AmbiguityAnalysisService.class);
        private static final ObjectMapper objectMapper = new ObjectMapper();

        private static final String ML_URL = "http://127.0.0.1:8000/analyze";

        private final RestTemplate restTemplate = new RestTemplate();

        /**
         * Calls the external ambiguity analysis service.
         *
         * @param text raw user message text
         * @return ambiguity analysis response (may be null if the downstream service
         *         returns an empty body)
         */
        public AmbiguityResponse analyze(String text) {
                AmbiguityRequest request = new AmbiguityRequest(text);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<AmbiguityRequest> entity = new HttpEntity<>(request, headers);

                ResponseEntity<AmbiguityResponse> response = restTemplate.postForEntity(
                                ML_URL,
                                entity,
                                AmbiguityResponse.class);
                AmbiguityResponse body = response.getBody();

                log.info("Ambiguity analysis response: status={}, body={}", response.getStatusCode().value(),
                                toJsonSafely(body));

                return body;
        }

        /**
         * Best-effort JSON serialization used for logging.
         *
         * <p>
         * This avoids throwing from logging paths when serialization fails.
         * </p>
         *
         * @param value value to serialize
         * @return JSON string if possible, otherwise {@code String.valueOf(value)}
         */
        private static String toJsonSafely(Object value) {
                if (value == null) {
                        return "null";
                }
                try {
                        return objectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                        return String.valueOf(value);
                }
        }
}
