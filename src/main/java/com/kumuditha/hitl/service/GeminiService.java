package com.kumuditha.hitl.service;

/*
 * File: GeminiService.java
 *
 * Description:
 * Wrapper around the Google Gemini SDK used to generate AI responses.
 *
 * Responsibilities:
 * - Lazily initializes and manages the Gemini client.
 * - Provides synchronous and streaming completion APIs.
 * - Handles failures with safe, user-facing fallback text.
 *
 * Used in:
 * - MessageService to generate assistant replies.
 */

import com.google.genai.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Consumer;

@Service
public class GeminiService {

    private volatile Client client;
    private volatile String apiKey;
    private final Object clientLock = new Object();
    private final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    /**
     * Creates the service and initializes the client if an API key is configured.
     *
     * @param apiKey Gemini API key from configuration (may be blank)
     */
    public GeminiService(@Value("${gemini.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Gemini API key not configured (gemini.api-key); GeminiService will run in fallback mode.");
            this.client = null;
        } else {
            initClient(apiKey);
        }
    }

    /**
     * Initializes or re-creates the underlying Gemini client.
     *
     * @param key API key (blank disables the client)
     */
    private void initClient(String key) {
        synchronized (clientLock) {
            if (key == null || key.isBlank()) {
                logger.warn("initClient called with empty key; disabling Gemini client.");
                this.client = null;
                return;
            }

            // Some SDK layers consult a system property; setting it keeps behavior
            // consistent.
            System.setProperty("GOOGLE_API_KEY", key);

            String masked = key.length() > 4 ? "***" + key.substring(key.length() - 4) : "***";
            logger.info("Initializing Gemini client with API key ending with: {}", masked);

            try {
                this.client = new Client();
            } catch (Exception e) {
                this.client = null;
                logger.error("Failed to initialize Gemini Client", e);
            }
        }
    }

    /**
     * Rotates the API key at runtime and reinitializes the client.
     *
     * @param newApiKey new key value (blank disables the client)
     */
    public void setApiKey(String newApiKey) {
        if (Objects.equals(this.apiKey, newApiKey)) {
            logger.debug("setApiKey called but key is unchanged (no-op)");
            return;
        }
        logger.info("Rotating Gemini API key (masked). Old: {}, New: {}",
                (this.apiKey == null ? "<none>"
                        : "***" + (this.apiKey.length() > 4 ? this.apiKey.substring(this.apiKey.length() - 4)
                                : this.apiKey)),
                (newApiKey == null ? "<none>"
                        : "***" + (newApiKey.length() > 4 ? newApiKey.substring(newApiKey.length() - 4) : newApiKey)));
        this.apiKey = newApiKey;
        initClient(newApiKey);
    }

    /**
     * Requests a single completion from Gemini.
     *
     * @param prompt prompt text to send to the model
     * @return the model's response text, or a fallback message if unavailable
     */
    public String getCompletion(String prompt) {

        try {
            if (this.client == null) {
                logger.warn("Gemini client unavailable when attempting to generate content; returning fallback.");
                return fallbackMessage();
            }

            try {
                String sysProp = System.getProperty("GOOGLE_API_KEY");
                String envVar = System.getenv("GOOGLE_API_KEY");
                logger.debug("GOOGLE_API_KEY (system property) endsWith: {}",
                        sysProp == null ? "<null>" : mask(sysProp));
                logger.debug("GOOGLE_API_KEY (env) endsWith: {}", envVar == null ? "<null>" : mask(envVar));
            } catch (Exception e) {
                logger.debug("Unable to read environment diagnostics: {}", e.toString());
            }

            var response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    prompt,
                    null);

            return response.text();

        } catch (Exception ex) {

            logger.error("[Gemini error] {}", ex.getMessage(), ex);

            String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (msg.contains("api key") || msg.contains("expired") || msg.contains("unauthorized")
                    || msg.contains("403") || msg.contains("400")) {
                logger.warn(
                        "Gemini request failed and indicates an authentication problem (message: {}). Verify gemini.api-key, restart the app, and ensure the key is valid.",
                        ex.getMessage());
            }

            return fallbackMessage();
        }
    }

    /**
     * Stream completion from Gemini and invoke the provided onChunk Consumer for
     * each partial response.
     * This uses the SDK's generateContentStream API and iterates the response
     * stream. The Consumer may be called
     * multiple times as the model emits partial output. If streaming isn't
     * available or an error occurs, the
     * consumer will be invoked once with the fallback message.
     */
    public void streamCompletion(String prompt, Consumer<String> onChunk) {
        if (this.client == null) {
            logger.warn("Gemini client unavailable; emitting fallback via onChunk");
            onChunk.accept(fallbackMessage());
            return;
        }

        try {
            var responseStream = client.models.generateContentStream(
                    "gemini-3-flash-preview",
                    prompt,
                    null);

            for (var partial : responseStream) {
                try {
                    String text = null;
                    try {
                        text = (String) partial.getClass().getMethod("text").invoke(partial);
                    } catch (NoSuchMethodException nsme) {
                        text = partial.toString();
                    }
                    if (text != null && !text.isEmpty()) {
                        onChunk.accept(text);
                    }
                } catch (Exception inner) {
                    logger.debug("Error extracting text from partial response: {}", inner.toString());
                }
            }

        } catch (Exception ex) {
            logger.error("[Gemini streaming error] {}", ex.getMessage(), ex);
            onChunk.accept(fallbackMessage());
        }
    }

    /**
     * Masks an API key-like string for safe logging.
     *
     * @param key raw key
     * @return masked key string showing only the last few characters
     */
    private static String mask(String key) {
        if (key == null)
            return "<null>";
        return key.length() <= 4 ? "***" + key : "***" + key.substring(key.length() - 4);
    }

    /**
     * Returns a safe fallback response for end users when the model is unavailable.
     *
     * @return fallback message
     */
    private String fallbackMessage() {
        return """
                I’m temporarily unable to generate a response due to high system load.
                Please try again in a moment. If the issue persists, your message has been saved and can be retried.
                """;
    }

}
