package com.kumuditha.hitl.dto;

import java.util.List;

public class ConversationDetailResponse {

    private Long id;
    private String title;
    private List<MessageDTO> messages;

    public ConversationDetailResponse() {
    }

    public ConversationDetailResponse(Long id, String title, List<MessageDTO> messages) {
        this.id = id;
        this.title = title;
        this.messages = messages;
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

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
