package com.kumuditha.hitl.controller;

import com.kumuditha.hitl.dto.CreateMessageRequest;
import com.kumuditha.hitl.dto.SendMessageResponse;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.service.MessageService;
import com.kumuditha.hitl.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private static final int LOG_LLM_OUTPUT_MAX_CHARS = 500;

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<SendMessageResponse> sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateMessageRequest request) {
        User user = userService.fromJwt(jwt);

        SendMessageResponse response = messageService.handleUserMessageWithAnalysis(
                user,
                request.getConversationId(),
                request.getContent());

        String llmOutput = response.getLlmOutput();
        String llmOutputPreview = (llmOutput == null)
                ? "<null>"
                : (llmOutput.length() <= LOG_LLM_OUTPUT_MAX_CHARS
                        ? llmOutput
                        : llmOutput.substring(0, LOG_LLM_OUTPUT_MAX_CHARS) + "...<truncated>");

        log.info("/api/messages response: conversationId={}, userMessageId={}, aiMessageId={}, llmOutputPreview={}",
                response.getConversationId(),
                response.getUserMessageId(),
                response.getAiMessageId(),
                llmOutputPreview);

        return ResponseEntity.ok(response);
    }
}
