package com.kumuditha.hitl.dto.ml;

/*
 * File: AmbiguityResponse.java
 *
 * Description:
 * Response DTO returned by the ambiguity analysis service.
 *
 * Responsibilities:
 * - Represents sentences, detected ambiguity items, and the final prompt if provided.
 *
 * Used in:
 * - AmbiguityAnalysisService and downstream prompt building.
 */

import java.util.List;

public class AmbiguityResponse {

    private String original_text;
    private List<String> sentences;
    private List<AmbiguityItem> ambiguities;
    private String final_prompt;

    /**
     * @return original input text (snake_case per ML service payload)
     */
    public String getOriginal_text() {
        return original_text;
    }

    /**
     * @param original_text original input text
     */
    public void setOriginal_text(String original_text) {
        this.original_text = original_text;
    }

    /**
     * @return list of sentences derived from the input text
     */
    public List<String> getSentences() {
        return sentences;
    }

    /**
     * @param sentences list of sentences derived from the input text
     */
    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }

    /**
     * @return detected ambiguity items
     */
    public List<AmbiguityItem> getAmbiguities() {
        return ambiguities;
    }

    /**
     * @param ambiguities detected ambiguity items
     */
    public void setAmbiguities(List<AmbiguityItem> ambiguities) {
        this.ambiguities = ambiguities;
    }

    /**
     * @return final prompt (if the ML service provides one)
     */
    public String getFinal_prompt() {
        return final_prompt;
    }

    /**
     * @param final_prompt final prompt
     */
    public void setFinal_prompt(String final_prompt) {
        this.final_prompt = final_prompt;
    }
}
