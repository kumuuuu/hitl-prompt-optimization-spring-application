package com.kumuditha.hitl.service;

import com.kumuditha.hitl.dto.ml.AmbiguityItem;
import com.kumuditha.hitl.dto.ml.AmbiguityResponse;
import com.kumuditha.hitl.dto.ml.AmbiguitySpan;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LlmPromptBuilder {

    public String buildPrompt(String userMessage, AmbiguityResponse ambiguity) {

        return """
You are an AI assistant responding to a user message that contains known ambiguities.
The ambiguities have already been detected by an external system.

Rules:
- Do NOT explain or restate ambiguities.
- Do NOT list interpretations.
- Do NOT ask clarifying questions.
- Provide a single, coherent answer that remains valid across all interpretations.
- Give safe, generally applicable guidance.

Ambiguity context (for internal reasoning only):
%s

User message:
%s
""".formatted(
                buildAmbiguityContext(ambiguity),
                userMessage
        );
    }

    private String buildAmbiguityContext(AmbiguityResponse ambiguity) {
        if (ambiguity.getAmbiguities() == null || ambiguity.getAmbiguities().isEmpty()) {
            return "No ambiguities detected.";
        }

        return ambiguity.getAmbiguities().stream()
                .map(this::formatAmbiguityItem)
                .collect(Collectors.joining("\n\n"));
    }

    private String formatAmbiguityItem(AmbiguityItem item) {

        StringBuilder sb = new StringBuilder();

        sb.append("Sentence: ")
                .append(item.getSentence())
                .append("\n");

        sb.append("Ambiguity types: ")
                .append(item.getClasses())
                .append("\n");

        if (item.getSpans() != null && !item.getSpans().isEmpty()) {
            sb.append("Ambiguous spans:\n");

            for (AmbiguitySpan span : item.getSpans()) {
                sb.append("- \"")
                        .append(span.getText())
                        .append("\" → ")
                        .append(span.getClassName());

                if (span.getSource() != null) {
                    sb.append(" (").append(span.getSource()).append(")");
                }

                sb.append("\n");
            }
        }

        return sb.toString().trim();
    }
}
