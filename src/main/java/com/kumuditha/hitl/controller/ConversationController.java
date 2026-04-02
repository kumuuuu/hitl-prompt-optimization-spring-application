package com.kumuditha.hitl.controller;

/*
 * File: ConversationController.java
 *
 * Description:
 * REST controller for listing and retrieving user conversations.
 *
 * Responsibilities:
 * - Exposes read-only conversation endpoints for the authenticated user.
 * - Maps domain/service results into API response DTOs.
 *
 * Used in:
 * - Frontend sidebar (conversation list) and conversation detail view.
 */

import com.kumuditha.hitl.dto.ConversationSummaryResponse;
import com.kumuditha.hitl.dto.ConversationDetailResponse;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.service.ConversationService;
import com.kumuditha.hitl.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

    private final ConversationService conversationService;
    private final UserService userService;

    /**
     * Creates a controller with required service dependencies.
     *
     * @param conversationService conversation read operations
     * @param userService         resolves/creates application users from JWT claims
     */
    public ConversationController(ConversationService conversationService, UserService userService) {
        this.conversationService = conversationService;
        this.userService = userService;
    }

    /**
     * Lists recent conversations for the authenticated user.
     *
     * <p>
     * Used to populate the UI sidebar. The {@code limit} parameter guards against
     * returning
     * excessively large lists.
     * </p>
     *
     * @param jwt   authenticated principal used to identify the user
     * @param limit maximum number of conversations to return
     * @return conversation summaries (most recent first)
     */
    @GetMapping
    public ResponseEntity<List<ConversationSummaryResponse>> listConversations(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "limit", required = false, defaultValue = "50") @Min(1) @Max(200) int limit) {
        User user = userService.fromJwt(jwt);

        logger.info("GET /api/conversations requested: userId={}, limit={}", user.getId(), limit);

        List<ConversationSummaryResponse> summaries = conversationService
                .listForUser(user, limit)
                .stream()
                .map(ConversationSummaryResponse::new)
                .toList();

        logger.info("GET /api/conversations returned: userId={}, count={}", user.getId(), summaries.size());

        return ResponseEntity.ok(summaries);
    }

    /**
     * Retrieves a single conversation (including its messages) for the
     * authenticated user.
     *
     * <p>
     * If the conversation does not exist or does not belong to the user, a 404 is
     * returned
     * to avoid leaking identifiers across users.
     * </p>
     *
     * @param jwt            authenticated principal used to identify the user
     * @param conversationId conversation identifier
     * @return conversation detail response or 404
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long conversationId) {
        User user = userService.fromJwt(jwt);
        logger.info("GET /api/conversations/{} requested: userId={}", conversationId, user.getId());

        return conversationService.getForUser(user, conversationId)
                .map(resp -> {
                    logger.info("GET /api/conversations/{} returned: userId={}, messageCount={}",
                            conversationId, user.getId(),
                            resp.getMessages() == null ? 0 : resp.getMessages().size());
                    return ResponseEntity.ok(resp);
                })
                .orElseGet(() -> {
                    logger.info("GET /api/conversations/{} not-found/unauthorized for userId={}", conversationId,
                            user.getId());
                    return ResponseEntity.notFound().build();
                });
    }
}
