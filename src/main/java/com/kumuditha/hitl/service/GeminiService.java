package com.kumuditha.hitl.service;

import com.google.genai.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;


@Service
public class GeminiService {

    private final Client client;
    private final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    public GeminiService(@Value("${gemini.api-key:}") String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Gemini API key not configured (gemini.api-key); GeminiService will run in fallback mode.");
            this.client = null;
        } else {
            System.setProperty("GOOGLE_API_KEY", apiKey);
            try {
                Map<String, String> env = System.getenv();
                Field field = env.getClass().getDeclaredField("m");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<String, String> writableEnv = (Map<String, String>) field.get(env);
                writableEnv.put("GOOGLE_API_KEY", apiKey);
            } catch (Exception e) {
                logger.debug("Could not set environment variable programmatically for GOOGLE_API_KEY: {}", e.toString());
            }

            this.client = new Client();
        }
    }

    public String getCompletion(String prompt) {

        try {
            var response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    prompt,
                    null
            );

            return response.text();

        } catch (Exception ex) {

            // Log internally for debugging
            System.err.println("[Gemini error] " + ex.getMessage());

            // User-safe fallback response
            return """
I’m temporarily unable to generate a response due to high system load.
Please try again in a moment. If the issue persists, your message has been saved and can be retried.
""";
        }
    }

}
