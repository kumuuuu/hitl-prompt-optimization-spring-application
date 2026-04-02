package com.kumuditha.hitl.service;

/*
 * File: OpenAIService.java
 *
 * Description:
 * Placeholder service for OpenAI integrations.
 *
 * Responsibilities:
 * - Provides a minimal completion API (currently mocked).
 *
 * Used in:
 * - Not currently wired; kept as an alternative provider/service stub.
 */

import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    /**
     * Returns a completion for the given prompt.
     *
     * <p>
     * Current behavior is a stubbed response to keep the API surface stable while
     * a different provider is used as the active LLM.
     * </p>
     *
     * @param prompt prompt text
     * @return completion text
     */
    public String getCompletion(String prompt) {
        return "AI response based on prompt:\n" + prompt;
    }
}
