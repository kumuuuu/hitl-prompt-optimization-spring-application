package com.kumuditha.hitl.repository;

import com.kumuditha.hitl.entity.Conversation;
import com.kumuditha.hitl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByUserOrderByUpdatedAtDesc(User user);
}
