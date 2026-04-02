package com.kumuditha.hitl.dto.ml;

/*
 * File: AmbiguityRequest.java
 *
 * Description:
 * Request DTO sent to the ambiguity analysis service.
 *
 * Responsibilities:
 * - Wraps the raw text to be analyzed.
 *
 * Used in:
 * - AmbiguityAnalysisService when calling the ML endpoint.
 */

public class AmbiguityRequest {
    private String text;

    /**
     * @param text raw text to analyze
     */
    public AmbiguityRequest(String text) {
        this.text = text;
    }

    /**
     * @return raw text to analyze
     */
    public String getText() {
        return text;
    }
}
