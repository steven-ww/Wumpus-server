package com.stevenww.wumpus.commentary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LangChainCommentaryGatewayTest {
    @Mock
    private WumpusCommentatorAiService commentatorAiService;

    @Test
    void shouldSerializeSnapshotAndDelegateToAiService() {
        when(commentatorAiService.comment(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn("Narrator quip.");
        LangChainCommentaryGateway gateway =
                new LangChainCommentaryGateway(commentatorAiService, new ObjectMapper());

        CommentaryRequest request = new CommentaryRequest(
                "MOVE",
                "MOVE_TO_ROOM",
                5,
                List.of(),
                5,
                "SAFE",
                5,
                List.of(1, 2, 3),
                List.of("You smell a Wumpus."),
                5,
                3,
                List.of("Moved to room 4"),
                null
        );

        String result = gateway.generateCommentary(request);

        ArgumentCaptor<String> snapshotCaptor = ArgumentCaptor.forClass(String.class);
        verify(commentatorAiService).comment(snapshotCaptor.capture());
        String snapshotJson = snapshotCaptor.getValue();
        assertTrue(snapshotJson.contains("\"action\":\"MOVE\""));
        assertTrue(snapshotJson.contains("\"outcome\":\"SAFE\""));
        assertEquals("Narrator quip.", result);
    }
}
