package com.kumuditha.hitl.service;

import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    public String getCompletion(String prompt) {

        // For now, mock response
        // We will wire real OpenAI call in next step
        return "AI response based on prompt:\n" + prompt;
    }
}
