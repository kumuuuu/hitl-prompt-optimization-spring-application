package com.kumuditha.hitl.dto.ml;

import java.util.List;

public class AmbiguityItem {

    private int sentence_index;
    private String sentence;
    private List<String> classes;
    private List<AmbiguitySpan> spans;

    public int getSentence_index() {
        return sentence_index;
    }

    public void setSentence_index(int sentence_index) {
        this.sentence_index = sentence_index;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<AmbiguitySpan> getSpans() {
        return spans;
    }

    public void setSpans(List<AmbiguitySpan> spans) {
        this.spans = spans;
    }
}
