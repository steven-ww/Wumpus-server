package com.stevenww.wumpus.prompt;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/prompt")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PromptResource {
    private final PromptService promptService;

    @Inject
    public PromptResource(PromptService promptService) {
        this.promptService = promptService;
    }

    @POST
    public PromptResponse prompt(PromptRequest request) {
        String context = request == null ? "" : request.getContext();
        return new PromptResponse(promptService.createPrompt(context));
    }
}

