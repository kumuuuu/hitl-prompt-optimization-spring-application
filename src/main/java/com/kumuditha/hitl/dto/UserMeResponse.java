package com.kumuditha.hitl.dto;

import com.kumuditha.hitl.entity.User;

public class UserMeResponse {

    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private String provider;

    public UserMeResponse(
            Long id,
            String email,
            String name,
            String avatarUrl,
            String provider
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
    }

    // Convenience constructor: create from entity
    public UserMeResponse(User user) {
        this(
                user != null ? user.getId() : null,
                user != null ? user.getEmail() : null,
                user != null ? user.getName() : null,
                user != null ? user.getAvatarUrl() : null,
                user != null ? user.getProvider() : null
        );
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getProvider() { return provider; }
}
