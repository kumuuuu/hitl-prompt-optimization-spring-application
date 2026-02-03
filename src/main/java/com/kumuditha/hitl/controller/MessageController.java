package com.kumuditha.hitl.controller;

import com.kumuditha.hitl.dto.CreateMessageRequest;
import com.kumuditha.hitl.entity.Conversation;
import com.kumuditha.hitl.entity.Message;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.service.MessageService;
import com.kumuditha.hitl.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<StreamingResponseBody> sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateMessageRequest request
    ) {
        User user = userService.fromJwt(jwt);

        // Prepare conversation and persist the user message immediately
        Conversation conversation = messageService.getOrCreateConversation(user, request.getConversationId());
        Message userMessage = messageService.saveUserMessage(conversation, request.getContent());

        // Build prompt now (may call ambiguity service synchronously)
        String prompt = messageService.buildPrompt(request.getContent());

        StreamingResponseBody stream = outputStream -> {
            try {
                // Generate AI text (currently synchronous; we will stream the returned text in chunks)
                String aiText = messageService.generateAiText(prompt);

                // Stream the AI text in small UTF-8 chunks so the client can incrementally decode
                final int CHUNK_SIZE = 32; // bytes per chunk; small enough for typing effect
                byte[] bytes = aiText.getBytes(StandardCharsets.UTF_8);
                int offset = 0;
                while (offset < bytes.length) {
                    int len = Math.min(CHUNK_SIZE, bytes.length - offset);
                    outputStream.write(bytes, offset, len);
                    outputStream.flush();
                    offset += len;
                    // No sleep here; the network flushing is sufficient. If desired, a short Thread.sleep could be added.
                }

                // Ensure newline at end
                outputStream.write('\n');
                outputStream.flush();

                // Persist the complete AI message after streaming
                messageService.saveAiMessage(conversation, aiText, prompt);

            } catch (Exception e) {
                // On error, attempt to write a short fallback message and persist an error-like AI message
                try {
                    String fallback = "\n[Error generating response]\n";
                    outputStream.write(fallback.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                } catch (Exception ignored) {
                }
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(stream);
    }
}
