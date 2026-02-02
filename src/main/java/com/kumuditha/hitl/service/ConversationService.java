package com.kumuditha.hitl.service;

import com.kumuditha.hitl.entity.Conversation;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.repository.ConversationRepository;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation createConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        return conversationRepository.save(conversation);
    }
}
