package com.kumuditha.hitl.service;

/*
 * File: ConversationService.java
 *
 * Description:
 * Service for conversation retrieval and conversation-to-DTO mapping.
 *
 * Responsibilities:
 * - Creates conversations and lists conversations for a given user.
 * - Enforces ownership checks when reading a specific conversation.
 * - Maps persisted Message entities to API-facing DTOs (including optional metadata).
 *
 * Used in:
 * - ConversationController to serve the sidebar list and conversation detail endpoints.
 */

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

    /**
     * Creates the service with required persistence dependencies.
     *
     * @param conversationRepository repository for Conversation entities
     * @param messageRepository      repository for Message entities
     */
    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Creates a new conversation owned by the given user.
     *
     * @param user owner of the new conversation
     * @return persisted conversation
     */
    public Conversation createConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        return conversationRepository.save(conversation);
    }

    /**
     * Lists conversations belonging to a user, capped to a safe maximum.
     *
     * @param user  owner whose conversations should be listed
     * @param limit requested maximum number of conversations
     * @return conversations ordered by most recently updated first
     */
    public List<Conversation> listForUser(User user, int limit) {
        int safeLimit = (limit <= 0) ? 50 : Math.min(limit, 200);

        List<Conversation> conversations = conversationRepository.findByUserOrderByUpdatedAtDesc(user);
        if (conversations.size() <= safeLimit) {
            return conversations;
        }
        return conversations.subList(0, safeLimit);
    }

    /**
     * Retrieves a conversation (and its messages) only if it belongs to the user.
     *
     * <p>
     * Returns empty instead of throwing when not found/not owned to avoid leaking
     * the existence of a conversation ID across users.
     * </p>
     *
     * @param user           requesting user
     * @param conversationId conversation identifier
     * @return a populated response if owned; otherwise empty
     */
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

    /**
     * Maps a persisted message into an API DTO, including optional ambiguity
     * metadata.
     *
     * @param message persisted message entity
     * @return message DTO expected by clients
     */
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

    /**
     * Converts internal sender type into the UI role strings expected by the
     * client.
     *
     * @param senderType sender type stored in the database
     * @return role string for the frontend message renderer
     */
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
