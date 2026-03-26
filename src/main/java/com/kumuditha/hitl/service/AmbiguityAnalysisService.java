package com.kumuditha.hitl.service;

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

        // "https://babyrider-hitl-prompt-optimization-api.hf.space/analyze/";

        private final RestTemplate restTemplate = new RestTemplate();

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
