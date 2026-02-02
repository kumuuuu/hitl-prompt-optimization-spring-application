package com.kumuditha.hitl.dto.ml;

public class AmbiguityRequest {
    private String text;

    public AmbiguityRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
