package com.kumuditha.hitl.repository;

/*
 * File: MessageRepository.java
 *
 * Description:
 * Spring Data JPA repository for Message entities.
 *
 * Responsibilities:
 * - Provides persistence operations and message lookup queries.
 *
 * Used in:
 * - MessageService for storing messages and ConversationService for loading message history.
 */

import com.kumuditha.hitl.entity.Message;
import com.kumuditha.hitl.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Returns messages for a conversation in chronological order.
     *
     * @param conversation conversation
     * @return messages ordered by {@code createdAt} ascending
     */
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
}
