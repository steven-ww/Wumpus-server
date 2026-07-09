package com.stevenww.wumpus.commentary;

public interface CommentaryGateway {
    String generateCommentary(CommentaryRequest request);

    default boolean isFallbackGateway() {
        return false;
    }
}
