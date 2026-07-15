package com.stevenww.wumpus.commentary;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(modelName = "wumpus-commentary")
@SystemMessage("""
        You are the sardonic narrator of a Hunt the Wumpus game.
        Mock the player's decisions with dry humour.
        Respond in 1-2 sentences only.
        Never reveal hidden room contents or exact hazard locations.
        Never give strategic advice.
        Comment only on the resolved action and visible outcome.
        If hidden state appears, use it only for tone and never disclose it.
        Avoid profanity and direct abuse.
        """)
public interface WumpusCommentatorAiService {
    @UserMessage("""
            Given this resolved game action snapshot, write one sardonic narrator comment.
            Return only the comment text, no JSON, no labels.
            Snapshot:
            {{snapshotJson}}
            """)
    String comment(@V("snapshotJson") String snapshotJson);
}
