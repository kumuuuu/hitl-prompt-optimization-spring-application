package com.kumuditha.hitl.dto.ml;

/*
 * File: AmbiguityItem.java
 *
 * Description:
 * Single ambiguity finding returned by the ML service.
 *
 * Responsibilities:
 * - Captures the ambiguous sentence, predicted ambiguity classes, confidences, and spans.
 *
 * Used in:
 * - LlmPromptBuilder to format ambiguity context for the LLM.
 */

import java.util.Map;
import java.util.List;

public class AmbiguityItem {

    private int sentence_index;
    private String sentence;
    private List<String> classes;
    private Map<String, Double> class_confidence;
    private List<AmbiguitySpan> spans;

    /**
     * @return sentence index in the original sentence list
     */
    public int getSentence_index() {
        return sentence_index;
    }

    /**
     * @param sentence_index sentence index in the original sentence list
     */
    public void setSentence_index(int sentence_index) {
        this.sentence_index = sentence_index;
    }

    /**
     * @return sentence text
     */
    public String getSentence() {
        return sentence;
    }

    /**
     * @param sentence sentence text
     */
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    /**
     * @return predicted ambiguity classes
     */
    public List<String> getClasses() {
        return classes;
    }

    /**
     * @param classes predicted ambiguity classes
     */
    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    /**
     * @return class confidence map
     */
    public Map<String, Double> getClass_confidence() {
        return class_confidence;
    }

    /**
     * @param class_confidence class confidence map
     */
    public void setClass_confidence(Map<String, Double> class_confidence) {
        this.class_confidence = class_confidence;
    }

    /**
     * @return ambiguous spans within the sentence
     */
    public List<AmbiguitySpan> getSpans() {
        return spans;
    }

    /**
     * @param spans ambiguous spans within the sentence
     */
    public void setSpans(List<AmbiguitySpan> spans) {
        this.spans = spans;
    }
}
