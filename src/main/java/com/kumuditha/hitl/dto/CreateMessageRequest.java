package com.kumuditha.hitl.dto;

/*
 * File: CreateMessageRequest.java
 *
 * Description:
 * API request DTO for submitting a new user message.
 *
 * Responsibilities:
 * - Carries the message content and an optional conversationId.
 *
 * Used in:
 * - MessageController POST /api/messages.
 */

import jakarta.validation.constraints.NotBlank;

public class CreateMessageRequest {

    private Long conversationId;

    @NotBlank
    private String content;

    /**
     * @return existing conversation ID, or null to start a new conversation
     */
    public Long getConversationId() {
        return conversationId;
    }

    /**
     * @param conversationId existing conversation ID, or null to start a new
     *                       conversation
     */
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * @return message content submitted by the user
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content message content submitted by the user
     */
    public void setContent(String content) {
        this.content = content;
    }
}
