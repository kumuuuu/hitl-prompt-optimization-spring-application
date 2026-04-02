package com.kumuditha.hitl.entity;

/*
 * File: User.java
 *
 * Description:
 * JPA entity representing an authenticated user of the application.
 *
 * Responsibilities:
 * - Stores identity-provider identifiers (Supabase subject) and basic profile fields.
 * - Tracks creation/update timestamps.
 *
 * Used in:
 * - UserService and controllers when persisting and returning the current user.
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Supabase subject/identifier (typically a UUID string). */
    @Column(nullable = false, unique = true)
    private String supabaseUserId;

    @Column(nullable = false)
    private String email;

    private String name;

    private String avatarUrl;

    /** Identity provider name (e.g., google, github). */
    @Column(nullable = true)
    private String provider;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Initializes audit timestamps on first persist.
     */
    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Updates the {@code updatedAt} audit timestamp before entity updates.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    /**
     * @return Supabase subject/identifier for this user
     */
    public String getSupabaseUserId() {
        return supabaseUserId;
    }

    /**
     * @param supabaseUserId Supabase subject/identifier for this user
     */
    public void setSupabaseUserId(String supabaseUserId) {
        this.supabaseUserId = supabaseUserId;
    }

    /**
     * @return email address associated with the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email email address associated with the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return display name (may be null)
     */
    public String getName() {
        return name;
    }

    /**
     * @param name display name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return avatar URL (may be null)
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * @param avatarUrl avatar URL
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * @return identity provider name (may be null)
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider identity provider name
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
