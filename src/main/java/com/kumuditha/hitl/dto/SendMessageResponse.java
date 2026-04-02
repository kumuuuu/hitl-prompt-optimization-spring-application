package com.kumuditha.hitl.dto;

/*
 * File: SendMessageResponse.java
 *
 * Description:
 * API response DTO returned after a user message is processed.
 *
 * Responsibilities:
 * - Provides conversation/message identifiers for client state updates.
 * - Carries ambiguity analysis and the final LLM output.
 *
 * Used in:
 * - MessageController POST /api/messages.
 */

import com.kumuditha.hitl.dto.ml.AmbiguityResponse;

public class SendMessageResponse {

    private Long conversationId;
    private Long userMessageId;
    private Long aiMessageId;

    private AmbiguityResponse analysis;

    /** Final LLM output text shown to the user. */
    private String llmOutput;

    /**
     * Default constructor for serializers.
     */
    public SendMessageResponse() {
    }

    /**
     * Creates a populated response.
     *
     * @param conversationId conversation identifier
     * @param userMessageId  stored user message identifier
     * @param aiMessageId    stored AI message identifier
     * @param analysis       ambiguity analysis output
     * @param llmOutput      final model output text
     */
    public SendMessageResponse(
            Long conversationId,
            Long userMessageId,
            Long aiMessageId,
            AmbiguityResponse analysis,
            String llmOutput) {
        this.conversationId = conversationId;
        this.userMessageId = userMessageId;
        this.aiMessageId = aiMessageId;
        this.analysis = analysis;
        this.llmOutput = llmOutput;
    }

    public Long getConversationId() {
        return conversationId;
    }

    /**
     * @param conversationId conversation identifier
     */
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * @return stored user message identifier
     */
    public Long getUserMessageId() {
        return userMessageId;
    }

    /**
     * @param userMessageId stored user message identifier
     */
    public void setUserMessageId(Long userMessageId) {
        this.userMessageId = userMessageId;
    }

    /**
     * @return stored AI message identifier
     */
    public Long getAiMessageId() {
        return aiMessageId;
    }

    /**
     * @param aiMessageId stored AI message identifier
     */
    public void setAiMessageId(Long aiMessageId) {
        this.aiMessageId = aiMessageId;
    }

    /**
     * @return ambiguity analysis output
     */
    public AmbiguityResponse getAnalysis() {
        return analysis;
    }

    /**
     * @param analysis ambiguity analysis output
     */
    public void setAnalysis(AmbiguityResponse analysis) {
        this.analysis = analysis;
    }

    /**
     * @return final model output text
     */
    public String getLlmOutput() {
        return llmOutput;
    }

    /**
     * @param llmOutput final model output text
     */
    public void setLlmOutput(String llmOutput) {
        this.llmOutput = llmOutput;
    }
}
