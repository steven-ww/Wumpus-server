package com.stevenww.wumpus.prompt;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EchoLlmGateway implements LlmGateway {
    @Override
    public String generatePrompt(String context) {
        return context;
    }
}

