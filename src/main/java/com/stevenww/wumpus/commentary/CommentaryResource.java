package com.stevenww.wumpus.commentary;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
    public CommentaryResponse commentary(CommentaryRequest request) {
        return commentaryService.createCommentary(request);
    }
}
