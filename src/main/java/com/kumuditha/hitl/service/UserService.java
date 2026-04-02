package com.kumuditha.hitl.service;

/*
 * File: UserService.java
 *
 * Description:
 * Service for resolving application users from Supabase JWT claims.
 *
 * Responsibilities:
 * - Finds existing users or creates new ones on first login.
 * - Maps identity-provider claims into the application's User entity.
 *
 * Used in:
 * - Controllers that need a normalized User object for the current request.
 */

import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Creates the service with required repository dependency.
     *
     * @param userRepository persistence access for users
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds an existing user by Supabase subject, or creates a new user record.
     *
     * <p>
     * This method is idempotent for a given {@code supabaseUserId}.
     * </p>
     *
     * @param supabaseUserId Supabase subject/identifier (typically a UUID string)
     * @param email          email claim (may be null depending on provider scopes)
     * @param name           display name (optional)
     * @param avatarUrl      avatar URL (optional)
     * @param provider       identity provider name (e.g., google, github)
     * @return persisted user entity
     */
    public User findOrCreateUser(
            String supabaseUserId,
            String email,
            String name,
            String avatarUrl,
            String provider) {
        return userRepository.findBySupabaseUserId(supabaseUserId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setSupabaseUserId(supabaseUserId);
                    user.setEmail(email);
                    user.setName(name);
                    user.setAvatarUrl(avatarUrl);
                    user.setProvider(provider);
                    return userRepository.save(user);
                });
    }

    /**
     * Convenience mapping from a JWT principal to an application {@link User}.
     *
     * @param jwt authenticated JWT (must not be null)
     * @return persisted user entity
     */
    public User fromJwt(Jwt jwt) {
        return findOrCreateUser(
                jwt.getSubject(),
                jwt.getClaim("email"),
                jwt.getClaim("name"),
                jwt.getClaim("avatar_url"),
                jwt.getClaim("provider"));
    }

}
