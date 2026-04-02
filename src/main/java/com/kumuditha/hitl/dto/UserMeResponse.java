package com.kumuditha.hitl.dto;

/*
 * File: UserMeResponse.java
 *
 * Description:
 * API response DTO representing the currently authenticated application user.
 *
 * Responsibilities:
 * - Normalizes identity-provider claims into a stable response shape.
 *
 * Used in:
 * - UserController GET /api/me.
 */

import com.kumuditha.hitl.entity.User;

public class UserMeResponse {

    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private String provider;

    /**
     * @param id        user identifier
     * @param email     email address
     * @param name      display name
     * @param avatarUrl avatar URL
     * @param provider  identity provider name
     */
    public UserMeResponse(
            Long id,
            String email,
            String name,
            String avatarUrl,
            String provider) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
    }

    /**
     * Convenience constructor that maps from a {@link User} entity.
     *
     * @param user persisted user entity
     */
    public UserMeResponse(User user) {
        this(
                user != null ? user.getId() : null,
                user != null ? user.getEmail() : null,
                user != null ? user.getName() : null,
                user != null ? user.getAvatarUrl() : null,
                user != null ? user.getProvider() : null);
    }

    /**
     * @return user identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return avatar URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * @return identity provider name
     */
    public String getProvider() {
        return provider;
    }
}
