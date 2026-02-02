package com.kumuditha.hitl.dto.ml;

import java.util.List;

public class AmbiguityResponse {

    private String original_text;
    private List<String> sentences;
    private List<AmbiguityItem> ambiguities;
    private String final_prompt;

    public String getOriginal_text() {
        return original_text;
    }

    public void setOriginal_text(String original_text) {
        this.original_text = original_text;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }

    public List<AmbiguityItem> getAmbiguities() {
        return ambiguities;
    }

    public void setAmbiguities(List<AmbiguityItem> ambiguities) {
        this.ambiguities = ambiguities;
    }

    public String getFinal_prompt() {
        return final_prompt;
    }

    public void setFinal_prompt(String final_prompt) {
        this.final_prompt = final_prompt;
    }
}
