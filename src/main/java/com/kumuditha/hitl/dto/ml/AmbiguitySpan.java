package com.kumuditha.hitl.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AmbiguitySpan {

    private String text;
    private int start;
    private int end;
    @JsonProperty("class")
    private String className;
    private String source;

    // JSON uses "class", which is a Java keyword
    // Jackson will map it if we rename the field

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
