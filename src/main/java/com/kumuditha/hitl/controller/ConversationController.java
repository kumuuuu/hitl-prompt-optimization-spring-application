package com.kumuditha.hitl.controller;

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

    public ConversationController(ConversationService conversationService, UserService userService) {
        this.conversationService = conversationService;
        this.userService = userService;
    }

    /**
     * Sidebar list.
     *
     * GET /api/conversations?limit=50
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
