package com.kumuditha.hitl.dto;

import com.kumuditha.hitl.entity.Conversation;

import java.time.Instant;

public class ConversationSummaryResponse {

    private Long id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public ConversationSummaryResponse() {
    }

    public ConversationSummaryResponse(Long id, String title, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ConversationSummaryResponse(Conversation conversation) {
        this(
                conversation != null ? conversation.getId() : null,
                conversation != null ? conversation.getTitle() : null,
                conversation != null ? conversation.getCreatedAt() : null,
                conversation != null ? conversation.getUpdatedAt() : null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
