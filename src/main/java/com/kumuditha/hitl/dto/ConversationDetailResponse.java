package com.kumuditha.hitl.dto;

/*
 * File: ConversationDetailResponse.java
 *
 * Description:
 * Full conversation DTO including message history.
 *
 * Responsibilities:
 * - Carries the conversation title and ordered list of messages.
 *
 * Used in:
 * - ConversationController GET /api/conversations/{conversationId}.
 */

import java.util.List;

public class ConversationDetailResponse {

    private Long id;
    private String title;
    private List<MessageDTO> messages;

    public ConversationDetailResponse() {
    }

    /**
     * @param id       conversation identifier
     * @param title    conversation title
     * @param messages ordered list of messages
     */
    public ConversationDetailResponse(Long id, String title, List<MessageDTO> messages) {
        this.id = id;
        this.title = title;
        this.messages = messages;
    }

    /**
     * @return conversation identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id conversation identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return conversation title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title conversation title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return ordered list of messages
     */
    public List<MessageDTO> getMessages() {
        return messages;
    }

    /**
     * @param messages ordered list of messages
     */
    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
