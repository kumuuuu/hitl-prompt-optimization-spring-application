package com.kumuditha.hitl.service;

import com.kumuditha.hitl.entity.Conversation;
import com.kumuditha.hitl.entity.Message;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.repository.ConversationRepository;
import com.kumuditha.hitl.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;
    private final AmbiguityAnalysisService ambiguityService;
//    private final OpenAIService openAIService;
    private final GeminiService geminiService;
    private final LlmPromptBuilder llmPromptBuilder;



    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            ConversationService conversationService,
            AmbiguityAnalysisService ambiguityService,
//            OpenAIService openAIService,
            GeminiService geminiService,
            LlmPromptBuilder llmPromptBuilder
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.conversationService = conversationService;
        this.ambiguityService = ambiguityService;
//        this.openAIService = openAIService;
        this.geminiService = geminiService;
        this.llmPromptBuilder = llmPromptBuilder;
    }


    public Message handleUserMessage(User user, Long conversationId, String content) {

        Conversation conversation =
                (conversationId == null)
                        ? conversationService.createConversation(user)
                        : conversationRepository.findById(conversationId)
                        .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        // 1. Save USER message
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setSender(Message.SenderType.USER);
        userMessage.setContent(content);
        messageRepository.save(userMessage);

        // 2. Call ML ambiguity service
        var ambiguityResult = ambiguityService.analyze(content);


        // 3. Build ambiguity-aware prompt (silent ambiguity handling)
        String llmPrompt =
                llmPromptBuilder.buildPrompt(content, ambiguityResult);

        // 4. Call Gemini
        String aiResponseText =
                geminiService.getCompletion(llmPrompt);

        // 4. Save AI message
        Message aiMessage = new Message();
        aiMessage.setConversation(conversation);
        aiMessage.setSender(Message.SenderType.AI);
        aiMessage.setContent(aiResponseText);
        aiMessage.setPromptUsed(llmPrompt);

        return messageRepository.save(aiMessage);
    }

}
