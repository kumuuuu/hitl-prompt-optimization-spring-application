package com.kumuditha.hitl.entity;

/*
 * File: Message.java
 *
 * Description:
 * JPA entity representing a single message in a conversation.
 *
 * Responsibilities:
 * - Stores message content, sender role, timestamps, and optional analysis metadata.
 * - Persists the prompt used to generate AI output for auditing/debugging.
 *
 * Used in:
 * - MessageService for persistence and ConversationService for DTO mapping.
 */

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {

    /**
     * Origin of the message content.
     */
    public enum SenderType {
        USER,
        AI,
        SYSTEM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String ambiguityResultJson;

    @Column(columnDefinition = "TEXT")
    private String promptUsed;

    public Long getId() {
        return id;
    }

    /**
     * @param id message identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return conversation that this message belongs to
     */
    public Conversation getConversation() {
        return conversation;
    }

    /**
     * @param conversation conversation that this message belongs to
     */
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    /**
     * @return sender type for this message
     */
    public SenderType getSender() {
        return sender;
    }

    /**
     * @param sender sender type for this message
     */
    public void setSender(SenderType sender) {
        this.sender = sender;
    }

    /**
     * @return message content text
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content message content text
     */
    public void setContent(String content) {
        this.content = content;
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
     * @return serialized ambiguity analysis JSON (may be null)
     */
    public String getAmbiguityResultJson() {
        return ambiguityResultJson;
    }

    /**
     * @param ambiguityResultJson serialized ambiguity analysis JSON
     */
    public void setAmbiguityResultJson(String ambiguityResultJson) {
        this.ambiguityResultJson = ambiguityResultJson;
    }

    /**
     * @return prompt used to generate assistant output (may be null)
     */
    public String getPromptUsed() {
        return promptUsed;
    }

    /**
     * @param promptUsed prompt used to generate assistant output
     */
    public void setPromptUsed(String promptUsed) {
        this.promptUsed = promptUsed;
    }
}
