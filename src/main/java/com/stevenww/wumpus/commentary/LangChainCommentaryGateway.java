package com.stevenww.wumpus.commentary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LangChainCommentaryGateway implements CommentaryGateway {
    private final WumpusCommentatorAiService commentatorAiService;
    private final ObjectMapper objectMapper;

    @Inject
    public LangChainCommentaryGateway(
            WumpusCommentatorAiService commentatorAiService,
            ObjectMapper objectMapper
    ) {
        this.commentatorAiService = commentatorAiService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateCommentary(CommentaryRequest request) {
        try {
            String snapshotJson = objectMapper.writeValueAsString(request);
            return commentatorAiService.comment(snapshotJson);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize commentary snapshot.", ex);
        }
    }
}
