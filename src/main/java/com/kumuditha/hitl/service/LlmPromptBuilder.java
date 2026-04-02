package com.kumuditha.hitl.service;

/*
 * File: LlmPromptBuilder.java
 *
 * Description:
 * Builds an LLM prompt that incorporates ambiguity analysis context.
 *
 * Responsibilities:
 * - Formats ambiguity items into a compact context block.
 * - Generates the final prompt with rules to keep responses robust across interpretations.
 *
 * Used in:
 * - MessageService to create prompts for the LLM based on ML ambiguity output.
 */

import com.kumuditha.hitl.dto.ml.AmbiguityItem;
import com.kumuditha.hitl.dto.ml.AmbiguityResponse;
import com.kumuditha.hitl.dto.ml.AmbiguitySpan;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LlmPromptBuilder {

    /**
     * Builds the full prompt sent to the LLM.
     *
     * <p>
     * The prompt instructs the model to produce an answer that remains valid across
     * multiple plausible interpretations, without explicitly listing ambiguities.
     * </p>
     *
     * @param userMessage raw user message
     * @param ambiguity   ambiguity analysis result
     * @return formatted prompt text
     */
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
                userMessage);
    }

    /**
     * Builds the ambiguity context section used for the prompt.
     *
     * @param ambiguity ambiguity analysis result
     * @return a human-readable context string
     */
    private String buildAmbiguityContext(AmbiguityResponse ambiguity) {
        if (ambiguity.getAmbiguities() == null || ambiguity.getAmbiguities().isEmpty()) {
            return "No ambiguities detected.";
        }

        return ambiguity.getAmbiguities().stream()
                .map(this::formatAmbiguityItem)
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * Formats a single ambiguity item into a stable, readable block.
     *
     * @param item ambiguity item
     * @return formatted text block
     */
    private String formatAmbiguityItem(AmbiguityItem item) {

        StringBuilder sb = new StringBuilder();

        sb.append("Sentence: ")
                .append(item.getSentence())
                .append("\n");

        sb.append("Ambiguity types: ")
                .append(item.getClasses())
                .append("\n");

        if (item.getClass_confidence() != null && !item.getClass_confidence().isEmpty()) {
            sb.append("Class confidence: ")
                    .append(item.getClass_confidence())
                    .append("\n");
        }

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
