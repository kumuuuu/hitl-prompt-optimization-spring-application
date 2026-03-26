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
import java.util.concurrent.atomic.AtomicBoolean;

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
            @Valid @RequestBody CreateMessageRequest request) {
        User user = userService.fromJwt(jwt);

        // Prepare conversation and persist the user message immediately
        Conversation conversation = messageService.getOrCreateConversation(user, request.getConversationId());
        Message userMessage = messageService.saveUserMessage(conversation, request.getContent());

        // Build prompt now (may call ambiguity service synchronously)
        String prompt = messageService.buildPrompt(request.getContent());

        StreamingResponseBody stream = outputStream -> {
            StringBuilder accumulated = new StringBuilder();
            AtomicBoolean anyChunkEmitted = new AtomicBoolean(false);

            try {
                // Stream from Gemini in real-time; MessageService.streamAiText will invoke the
                // callback for each partial chunk
                messageService.streamAiText(prompt, chunk -> {
                    try {
                        if (chunk == null)
                            return;
                        byte[] bytes = chunk.getBytes(StandardCharsets.UTF_8);
                        // write the chunk directly and flush so client can decode progressively
                        outputStream.write(bytes);
                        outputStream.flush();

                        accumulated.append(chunk);
                        anyChunkEmitted.set(true);
                    } catch (Exception e) {
                        // If writing failed, log on server side (can't throw from lambda)
                        System.err.println("Failed to write chunk to output stream: " + e.getMessage());
                    }
                });

                // If no chunks were emitted, write a minimal newline so client isn't left
                // waiting
                if (!anyChunkEmitted.get()) {
                    outputStream.write('\n');
                    outputStream.flush();
                } else {
                    // terminate with newline
                    outputStream.write('\n');
                    outputStream.flush();
                }

                // Persist the complete AI message after streaming
                String aiText = accumulated.toString();
                messageService.saveAiMessage(conversation, aiText, prompt);

            } catch (Exception e) {
                // On error, attempt to write a short fallback message
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
