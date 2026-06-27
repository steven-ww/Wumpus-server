package com.stevenww.wumpus.prompt;

public class PromptRequest {
    private String context;

    public PromptRequest() {
    }

    public PromptRequest(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}

