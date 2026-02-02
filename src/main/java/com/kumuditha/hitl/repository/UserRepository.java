package com.kumuditha.hitl.repository;

import com.kumuditha.hitl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySupabaseUserId(String supabaseUserId);

    Optional<User> findByEmail(String email);
}
