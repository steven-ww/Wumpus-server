package com.stevenww.wumpus.prompt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PromptService {
    private final LlmGateway llmGateway;

    @Inject
    public PromptService(LlmGateway llmGateway) {
        this.llmGateway = llmGateway;
    }

    public String createPrompt(String context) {
        String safeContext = context == null ? "" : context;
        return llmGateway.generatePrompt(safeContext);
    }
}

