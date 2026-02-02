package com.kumuditha.hitl.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateMessageRequest {

    private Long conversationId;

    @NotBlank
    private String content;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
