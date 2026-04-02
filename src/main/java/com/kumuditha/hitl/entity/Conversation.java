package com.kumuditha.hitl.entity;

/*
 * File: Conversation.java
 *
 * Description:
 * JPA entity representing a chat conversation owned by a user.
 *
 * Responsibilities:
 * - Stores conversation metadata (title, timestamps) and ownership relation.
 * - Tracks last-updated time for ordering in UI lists.
 *
 * Used in:
 * - MessageService and ConversationService to group messages per user session.
 */

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(length = 255)
    private String title;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    /**
     * Updates the {@code updatedAt} timestamp before entity updates.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

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
     * @return owner of this conversation
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user owner of this conversation
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return conversation title (may be null)
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
