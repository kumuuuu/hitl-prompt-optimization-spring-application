package com.kumuditha.hitl.service;

import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Called after successful Supabase authentication
     */
    public User findOrCreateUser(
            String supabaseUserId,
            String email,
            String name,
            String avatarUrl,
            String provider
    ) {
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

    public User fromJwt(Jwt jwt) {
        return findOrCreateUser(
                jwt.getSubject(),
                jwt.getClaim("email"),
                jwt.getClaim("name"),
                jwt.getClaim("avatar_url"),
                jwt.getClaim("provider")
        );
    }


}
