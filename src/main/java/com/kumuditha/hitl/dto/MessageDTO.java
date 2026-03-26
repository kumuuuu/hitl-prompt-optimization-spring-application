package com.kumuditha.hitl.dto;

import java.util.Map;

public class MessageDTO {

    private String role; // "user" | "assistant"
    private String content;
    private String timestamp; // ISO-8601 string
    private Map<String, Object> meta; // optional

    public MessageDTO() {
    }

    public MessageDTO(String role, String content, String timestamp, Map<String, Object> meta) {
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
        this.meta = meta;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }
}
