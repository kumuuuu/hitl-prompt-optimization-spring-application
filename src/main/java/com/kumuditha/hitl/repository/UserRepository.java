package com.kumuditha.hitl.repository;

/*
 * File: UserRepository.java
 *
 * Description:
 * Spring Data JPA repository for User entities.
 *
 * Responsibilities:
 * - Provides persistence operations and common lookup queries.
 *
 * Used in:
 * - UserService during user resolution/creation.
 */

import com.kumuditha.hitl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Looks up a user by Supabase subject/identifier.
     *
     * @param supabaseUserId Supabase subject
     * @return user if present
     */
    Optional<User> findBySupabaseUserId(String supabaseUserId);

    /**
     * Looks up a user by email.
     *
     * @param email email address
     * @return user if present
     */
    Optional<User> findByEmail(String email);
}
