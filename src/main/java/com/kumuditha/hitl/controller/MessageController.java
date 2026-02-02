package com.kumuditha.hitl.controller;

import com.kumuditha.hitl.dto.CreateMessageRequest;
import com.kumuditha.hitl.entity.Message;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.service.MessageService;
import com.kumuditha.hitl.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping
    public Message sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateMessageRequest request
    ) {
        User user = userService.fromJwt(jwt);

        return messageService.handleUserMessage(
                user,
                request.getConversationId(),
                request.getContent()
        );

    }
}
