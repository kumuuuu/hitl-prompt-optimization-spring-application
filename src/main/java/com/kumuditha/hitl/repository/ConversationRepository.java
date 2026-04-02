package com.kumuditha.hitl.repository;

/*
 * File: ConversationRepository.java
 *
 * Description:
 * Spring Data JPA repository for Conversation entities.
 *
 * Responsibilities:
 * - Provides persistence operations and common lookup queries.
 *
 * Used in:
 * - ConversationService and MessageService for conversation access.
 */

import com.kumuditha.hitl.entity.Conversation;
import com.kumuditha.hitl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Lists conversations owned by a user ordered by most recently updated first.
     *
     * @param user owner
     * @return conversations ordered by {@code updatedAt} descending
     */
    List<Conversation> findByUserOrderByUpdatedAtDesc(User user);
}
