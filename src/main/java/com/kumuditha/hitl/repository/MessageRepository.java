package com.kumuditha.hitl.repository;

import com.kumuditha.hitl.entity.Message;
import com.kumuditha.hitl.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
}
