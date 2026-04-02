package com.kumuditha.hitl.service;

/*
 * File: MessageService.java
 *
 * Description:
 * Service that orchestrates message persistence, ambiguity analysis, and LLM generation.
 *
 * Responsibilities:
 * - Creates/reuses conversations for incoming user messages.
 * - Persists user and AI messages.
 * - Runs ambiguity analysis and builds prompts for the LLM.
 * - Supports both non-streaming and streaming AI responses.
 *
 * Used in:
 * - MessageController to handle message submission and response generation.
 */

import com.kumuditha.hitl.entity.Conversation;
import com.kumuditha.hitl.entity.Message;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.repository.ConversationRepository;
import com.kumuditha.hitl.repository.MessageRepository;
import org.springframework.stereotype.Service;

import com.kumuditha.hitl.dto.SendMessageResponse;
import com.kumuditha.hitl.dto.ml.AmbiguityResponse;

import java.util.function.Consumer;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;
    private final AmbiguityAnalysisService ambiguityService;
    private final LlmService llmService;
    private final LlmPromptBuilder llmPromptBuilder;

    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            ConversationService conversationService,
            AmbiguityAnalysisService ambiguityService,
            LlmService llmService,
            LlmPromptBuilder llmPromptBuilder) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.conversationService = conversationService;
        this.ambiguityService = ambiguityService;
        this.llmService = llmService;
        this.llmPromptBuilder = llmPromptBuilder;
    }

    /**
     * Legacy non-streaming flow that stores both user and AI messages.
     *
     * <p>
     * This method performs ambiguity analysis and uses the resulting prompt to
     * obtain
     * an LLM completion before persisting the assistant message.
     * </p>
     *
     * @param user           authenticated user
     * @param conversationId existing conversation ID, or null to create a new
     *                       conversation
     * @param content        user message content
     * @return persisted AI message
     */
    public Message handleUserMessage(User user, Long conversationId, String content) {

        Conversation conversation = (conversationId == null)
                ? conversationService.createConversation(user)
                : conversationRepository.findById(conversationId)
                        .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setSender(Message.SenderType.USER);
        userMessage.setContent(content);
        messageRepository.save(userMessage);

        var ambiguityResult = ambiguityService.analyze(content);

        String llmPrompt = llmPromptBuilder.buildPrompt(content, ambiguityResult);

        String aiResponseText = llmService.generate(llmPrompt);

        Message aiMessage = new Message();
        aiMessage.setConversation(conversation);
        aiMessage.setSender(Message.SenderType.AI);
        aiMessage.setContent(aiResponseText);
        aiMessage.setPromptUsed(llmPrompt);

        return messageRepository.save(aiMessage);
    }

    /**
     * Non-streaming flow used by the API endpoint.
     *
     * <p>
     * Persists the user message, runs ambiguity analysis, builds an ambiguity-aware
     * prompt,
     * calls the LLM, persists the AI message, and returns both analysis + raw LLM
     * output.
     * </p>
     *
     * @param user           authenticated user
     * @param conversationId existing conversation ID, or null to create a new
     *                       conversation
     * @param content        user message content
     * @return response containing message IDs, analysis, and LLM output
     */
    public SendMessageResponse handleUserMessageWithAnalysis(User user, Long conversationId, String content) {

        Conversation conversation = getOrCreateConversation(user, conversationId);

        Message userMessage = saveUserMessage(conversation, content);

        AmbiguityResponse analysis = ambiguityService.analyze(content);
        String llmPrompt = llmPromptBuilder.buildPrompt(content, analysis);
        String llmOutput = llmService.generate(llmPrompt);

        Message aiMessage = saveAiMessage(conversation, llmOutput, llmPrompt);

        return new SendMessageResponse(
                conversation.getId(),
                userMessage.getId(),
                aiMessage.getId(),
                analysis,
                llmOutput);
    }

    /**
     * Loads an existing conversation by ID, or creates a new one when no ID is
     * provided.
     *
     * @param user           owner of the conversation
     * @param conversationId conversation identifier (nullable)
     * @return an existing or newly created conversation
     */
    public Conversation getOrCreateConversation(User user, Long conversationId) {
        return (conversationId == null)
                ? conversationService.createConversation(user)
                : conversationRepository.findById(conversationId)
                        .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
    }

    /**
     * Persists a new user-authored message in the given conversation.
     *
     * @param conversation conversation to associate the message with
     * @param content      message content
     * @return persisted message
     */
    public Message saveUserMessage(Conversation conversation, String content) {
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setSender(Message.SenderType.USER);
        userMessage.setContent(content);
        return messageRepository.save(userMessage);
    }

    /**
     * Builds an LLM prompt for the given user content using ambiguity analysis.
     *
     * @param content raw user content
     * @return prompt to send to the LLM
     */
    public String buildPrompt(String content) {
        var ambiguityResult = ambiguityService.analyze(content);
        return llmPromptBuilder.buildPrompt(content, ambiguityResult);
    }

    /**
     * Generates AI text for an already-built prompt.
     *
     * @param prompt prompt to send to the LLM
     * @return LLM completion text
     */
    public String generateAiText(String prompt) {
        return llmService.generate(prompt);
    }

    /**
     * Streams AI output via callback as partial chunks arrive from the underlying
     * LLM.
     *
     * @param prompt  prompt to send to the LLM
     * @param onChunk consumer invoked for each partial chunk emitted
     */
    public void streamAiText(String prompt, Consumer<String> onChunk) {
        // Ollama call is non-streaming; emit as a single chunk.
        onChunk.accept(llmService.generate(prompt));
    }

    /**
     * Persists a new assistant-authored message.
     *
     * @param conversation conversation to associate the message with
     * @param aiText       assistant output text
     * @param promptUsed   prompt that produced this output (for auditing/debugging)
     * @return persisted message
     */
    public Message saveAiMessage(Conversation conversation, String aiText, String promptUsed) {
        Message aiMessage = new Message();
        aiMessage.setConversation(conversation);
        aiMessage.setSender(Message.SenderType.AI);
        aiMessage.setContent(aiText);
        aiMessage.setPromptUsed(promptUsed);
        return messageRepository.save(aiMessage);
    }

}
