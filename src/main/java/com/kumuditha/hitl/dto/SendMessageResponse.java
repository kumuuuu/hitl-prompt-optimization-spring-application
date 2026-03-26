package com.kumuditha.hitl.dto;

import com.kumuditha.hitl.dto.ml.AmbiguityResponse;

public class SendMessageResponse {

    private Long conversationId;
    private Long userMessageId;
    private Long aiMessageId;

    private AmbiguityResponse analysis;

    /**
     * Final LLM output text shown to the user.
     */
    private String llmOutput;

    public SendMessageResponse() {
    }

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

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getUserMessageId() {
        return userMessageId;
    }

    public void setUserMessageId(Long userMessageId) {
        this.userMessageId = userMessageId;
    }

    public Long getAiMessageId() {
        return aiMessageId;
    }

    public void setAiMessageId(Long aiMessageId) {
        this.aiMessageId = aiMessageId;
    }

    public AmbiguityResponse getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AmbiguityResponse analysis) {
        this.analysis = analysis;
    }

    public String getLlmOutput() {
        return llmOutput;
    }

    public void setLlmOutput(String llmOutput) {
        this.llmOutput = llmOutput;
    }
}
