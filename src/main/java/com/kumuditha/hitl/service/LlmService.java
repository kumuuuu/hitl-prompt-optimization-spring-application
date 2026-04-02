package com.kumuditha.hitl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    private static final URI OLLAMA_GENERATE_URI = URI.create("http://localhost:11434/api/generate");
    private static final String MODEL = "mistral";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LlmService() {
        this.restTemplate = buildRestTemplate(Duration.ofSeconds(5), Duration.ofSeconds(60));
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates text from the local Ollama model.
     *
     * @param prompt prompt to send to the model
     * @return generated text (the "response" field from Ollama)
     */
    public String generate(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("prompt must not be blank");
        }

        OllamaGenerateRequest request = new OllamaGenerateRequest(MODEL, prompt, false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OllamaGenerateRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(OLLAMA_GENERATE_URI, entity, String.class);
        } catch (ResourceAccessException ex) {
            // Typically thrown for I/O problems (connect/read timeouts, connection refused,
            // etc.)
            throw new IllegalStateException("Ollama request failed (timeout / connection error)", ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Ollama request failed", ex);
        }

        if (response == null) {
            throw new IllegalStateException("Ollama request failed: empty response");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            String bodySnippet = safeSnippet(response.getBody());
            throw new IllegalStateException(
                    "Ollama request failed: HTTP " + response.getStatusCode().value() + " body=" + bodySnippet);
        }

        String body = response.getBody();
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("Ollama response body was empty");
        }

        try {
            JsonNode root = objectMapper.readTree(body);
            String text = root.path("response").asText(null);
            if (text == null) {
                throw new IllegalStateException("Ollama response missing 'response' field");
            }
            return text;
        } catch (JsonProcessingException ex) {
            log.warn("Failed to parse Ollama JSON response: {}", safeSnippet(body));
            throw new IllegalStateException("Failed to parse Ollama response JSON", ex);
        }
    }

    private static String safeSnippet(String body) {
        if (body == null) {
            return "<null>";
        }
        String trimmed = body.trim();
        if (trimmed.length() <= 500) {
            return trimmed;
        }
        return trimmed.substring(0, 500) + "…";
    }

    private static RestTemplate buildRestTemplate(Duration connectTimeout, Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) connectTimeout.toMillis());
        requestFactory.setReadTimeout((int) readTimeout.toMillis());
        return new RestTemplate(requestFactory);
    }

    private record OllamaGenerateRequest(String model, String prompt, boolean stream) {
    }
}
