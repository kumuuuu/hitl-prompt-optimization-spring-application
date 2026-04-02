package com.kumuditha.hitl.dto;

/*
 * File: ConversationSummaryResponse.java
 *
 * Description:
 * Lightweight conversation DTO used for list views.
 *
 * Responsibilities:
 * - Provides a minimal set of fields required by the conversation sidebar.
 *
 * Used in:
 * - ConversationController GET /api/conversations.
 */

import com.kumuditha.hitl.entity.Conversation;

import java.time.Instant;

public class ConversationSummaryResponse {

    private Long id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public ConversationSummaryResponse() {
    }

    /**
     * @param id        conversation identifier
     * @param title     conversation title
     * @param createdAt creation timestamp
     * @param updatedAt last update timestamp
     */
    public ConversationSummaryResponse(Long id, String title, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Convenience constructor that maps from a {@link Conversation} entity.
     *
     * @param conversation persisted conversation entity
     */
    public ConversationSummaryResponse(Conversation conversation) {
        this(
                conversation != null ? conversation.getId() : null,
                conversation != null ? conversation.getTitle() : null,
                conversation != null ? conversation.getCreatedAt() : null,
                conversation != null ? conversation.getUpdatedAt() : null);
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
     * @return creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return last update timestamp
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt last update timestamp
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
