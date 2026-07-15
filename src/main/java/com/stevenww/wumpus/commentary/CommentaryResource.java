package com.stevenww.wumpus.commentary;

import org.eclipse.microprofile.faulttolerance.Fallback;
import io.smallrye.faulttolerance.api.RateLimit;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.temporal.ChronoUnit;

@Path("/api/commentary")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentaryResource {
    private final CommentaryService commentaryService;

    @Inject
    public CommentaryResource(CommentaryService commentaryService) {
        this.commentaryService = commentaryService;
    }

    @POST
    @Fallback(fallbackMethod = "rateLimitedCommentary")
    @RateLimit(value = 1, window = 1, windowUnit = ChronoUnit.SECONDS)
    public CommentaryResponse commentary(CommentaryRequest request) {
        return commentaryService.createCommentary(request);
    }

    public CommentaryResponse rateLimitedCommentary(CommentaryRequest request) {
        return commentaryService.rateLimitedCommentary(request);
    }
}
