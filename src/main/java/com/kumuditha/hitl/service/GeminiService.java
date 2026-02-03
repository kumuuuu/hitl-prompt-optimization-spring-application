package com.kumuditha.hitl.service;

import com.google.genai.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class GeminiService {

    private volatile Client client;
    private volatile String apiKey;
    private final Object clientLock = new Object();
    private final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    public GeminiService(@Value("${gemini.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Gemini API key not configured (gemini.api-key); GeminiService will run in fallback mode.");
            this.client = null;
        } else {
            // initialize client with the provided key
            initClient(apiKey);
        }
    }

    // Initialize or re-create the client with a given API key.
    private void initClient(String key) {
        synchronized (clientLock) {
            if (key == null || key.isBlank()) {
                logger.warn("initClient called with empty key; disabling Gemini client.");
                this.client = null;
                return;
            }

            // Set system property so underlying libs that read it will find it.
            System.setProperty("GOOGLE_API_KEY", key);

            // Log masked key for diagnostics (never log full key in production)
            String masked = key.length() > 4 ? "***" + key.substring(key.length() - 4) : "***";
            logger.info("Initializing Gemini client with API key ending with: {}", masked);

            // Do NOT attempt to modify the process environment via reflection (not portable).
            try {
                this.client = new Client();
            } catch (Exception e) {
                this.client = null;
                logger.error("Failed to initialize Gemini Client", e);
            }
        }
    }

    // Allow runtime key rotation: caller can update the key and the client will be recreated.
    public void setApiKey(String newApiKey) {
        if (Objects.equals(this.apiKey, newApiKey)) {
            logger.debug("setApiKey called but key is unchanged (no-op)");
            return;
        }
        logger.info("Rotating Gemini API key (masked). Old: {}, New: {}",
                (this.apiKey == null ? "<none>" : "***" + (this.apiKey.length() > 4 ? this.apiKey.substring(this.apiKey.length() - 4) : this.apiKey)),
                (newApiKey == null ? "<none>" : "***" + (newApiKey.length() > 4 ? newApiKey.substring(newApiKey.length() - 4) : newApiKey)));
        this.apiKey = newApiKey;
        initClient(newApiKey);
    }

    public String getCompletion(String prompt) {

        try {
            if (this.client == null) {
                logger.warn("Gemini client unavailable when attempting to generate content; returning fallback.");
                return fallbackMessage();
            }

            // Diagnostic: log which key is in system properties and env (masked)
            try {
                String sysProp = System.getProperty("GOOGLE_API_KEY");
                String envVar = System.getenv("GOOGLE_API_KEY");
                logger.debug("GOOGLE_API_KEY (system property) endsWith: {}", sysProp == null ? "<null>" : mask(sysProp));
                logger.debug("GOOGLE_API_KEY (env) endsWith: {}", envVar == null ? "<null>" : mask(envVar));
            } catch (Exception e) {
                logger.debug("Unable to read environment diagnostics: {}", e.toString());
            }

            var response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    prompt,
                    null
            );

            return response.text();

        } catch (Exception ex) {

            // Log internally for debugging with stacktrace
            logger.error("[Gemini error] {}", ex.getMessage(), ex);

            // If the error message suggests an API key issue, log extra guidance
            String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (msg.contains("api key") || msg.contains("expired") || msg.contains("unauthorized") || msg.contains("403") || msg.contains("400")) {
                logger.warn("Gemini request failed and indicates an authentication problem (message: {}). Verify gemini.api-key, restart the app, and ensure the key is valid.", ex.getMessage());
            }

            // User-safe fallback response
            return fallbackMessage();
        }
    }

    private static String mask(String key) {
        if (key == null) return "<null>";
        return key.length() <= 4 ? "***" + key : "***" + key.substring(key.length() - 4);
    }

    private String fallbackMessage() {
        return """
I’m temporarily unable to generate a response due to high system load.
Please try again in a moment. If the issue persists, your message has been saved and can be retried.
""";
    }

}
