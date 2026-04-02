package com.kumuditha.hitl.dto;

/*
 * File: MessageDTO.java
 *
 * Description:
 * Client-facing message DTO used in conversation detail responses.
 *
 * Responsibilities:
 * - Represents a message in a format expected by the frontend renderer.
 * - Optionally includes metadata such as ambiguity analysis results.
 *
 * Used in:
 * - ConversationDetailResponse messages list.
 */

import java.util.Map;

public class MessageDTO {

    /** Frontend role string (e.g., "user" or "assistant"). */
    private String role;
    private String content;
    /** ISO-8601 timestamp string. */
    private String timestamp;
    /** Optional metadata (e.g., analysis results) for the client. */
    private Map<String, Object> meta;

    public MessageDTO() {
    }

    /**
     * @param role      frontend role string
     * @param content   message content
     * @param timestamp ISO-8601 timestamp string
     * @param meta      optional metadata
     */
    public MessageDTO(String role, String content, String timestamp, Map<String, Object> meta) {
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
        this.meta = meta;
    }

    /**
     * @return frontend role string
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role frontend role string
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return message content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content message content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return ISO-8601 timestamp string
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp ISO-8601 timestamp string
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return optional metadata
     */
    public Map<String, Object> getMeta() {
        return meta;
    }

    /**
     * @param meta optional metadata
     */
    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }
}
