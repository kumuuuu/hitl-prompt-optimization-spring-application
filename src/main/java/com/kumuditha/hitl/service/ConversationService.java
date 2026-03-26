package com.kumuditha.hitl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuditha.hitl.dto.ConversationDetailResponse;
import com.kumuditha.hitl.dto.MessageDTO;
import com.kumuditha.hitl.dto.ml.AmbiguityResponse;
import com.kumuditha.hitl.entity.Conversation;
import com.kumuditha.hitl.entity.Message;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.repository.ConversationRepository;
import com.kumuditha.hitl.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public Conversation createConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        return conversationRepository.save(conversation);
    }

    public List<Conversation> listForUser(User user, int limit) {
        int safeLimit = (limit <= 0) ? 50 : Math.min(limit, 200);

        List<Conversation> conversations = conversationRepository.findByUserOrderByUpdatedAtDesc(user);
        if (conversations.size() <= safeLimit) {
            return conversations;
        }
        return conversations.subList(0, safeLimit);
    }

    public Optional<ConversationDetailResponse> getForUser(User user, Long conversationId) {
        Optional<Conversation> convOpt = conversationRepository.findById(conversationId);
        if (convOpt.isEmpty()) {
            logger.info("getForUser: conversation not found: conversationId={}, userId={}", conversationId,
                    user.getId());
            return Optional.empty();
        }

        Conversation conversation = convOpt.get();
        Long ownerId = (conversation.getUser() == null) ? null : conversation.getUser().getId();
        if (ownerId == null || !ownerId.equals(user.getId())) {
            logger.info(
                    "getForUser: conversation not owned by user: conversationId={}, requestedUserId={}, ownerUserId={}",
                    conversationId, user.getId(), ownerId);
            return Optional.empty();
        }

        List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
        List<MessageDTO> messageDtos = messages.stream().map(this::toDto).toList();

        return Optional.of(new ConversationDetailResponse(
                conversation.getId(),
                conversation.getTitle(),
                messageDtos));
    }

    private MessageDTO toDto(Message message) {
        String role = toRole(message.getSender());
        String timestamp = message.getCreatedAt() != null ? message.getCreatedAt().toString() : null;

        Map<String, Object> meta = null;
        if (message.getAmbiguityResultJson() != null && !message.getAmbiguityResultJson().isBlank()) {
            try {
                AmbiguityResponse analysis = objectMapper.readValue(message.getAmbiguityResultJson(),
                        AmbiguityResponse.class);
                meta = Map.of(
                        "ambiguities",
                        analysis != null && analysis.getAmbiguities() != null ? analysis.getAmbiguities() : List.of());
            } catch (Exception e) {
                logger.info("Failed to parse ambiguityResultJson for messageId={}: {}", message.getId(),
                        e.getMessage());
            }
        }

        return new MessageDTO(role, message.getContent(), timestamp, meta);
    }

    private static String toRole(Message.SenderType senderType) {
        if (senderType == null) {
            return "assistant";
        }
        return switch (senderType) {
            case USER -> "user";
            case AI -> "assistant";
            case SYSTEM -> "assistant";
        };
    }
}
