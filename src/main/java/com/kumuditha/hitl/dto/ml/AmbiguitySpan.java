package com.kumuditha.hitl.dto.ml;

/*
 * File: AmbiguitySpan.java
 *
 * Description:
 * Span-level detail for a detected ambiguity in a sentence.
 *
 * Responsibilities:
 * - Identifies the ambiguous substring with start/end offsets and class label.
 *
 * Used in:
 * - LlmPromptBuilder when rendering ambiguity context.
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmbiguitySpan {

    private String text;
    private int start;
    private int end;

    /**
     * Ambiguity class label.
     *
     * <p>
     * The JSON field name is {@code "class"}, but Java reserves {@code class} as a
     * keyword,
     * so it is mapped to {@code className}.
     * </p>
     */
    @JsonProperty("class")
    private String className;
    private String source;

    /**
     * @return ambiguous text segment
     */
    public String getText() {
        return text;
    }

    /**
     * @param text ambiguous text segment
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return start offset (inclusive)
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start start offset (inclusive)
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return end offset (exclusive)
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end end offset (exclusive)
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @return ambiguity class label
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className ambiguity class label
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return model/source identifier (if provided by the ML service)
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source model/source identifier
     */
    public void setSource(String source) {
        this.source = source;
    }
}
