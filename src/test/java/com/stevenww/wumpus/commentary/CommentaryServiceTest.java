package com.stevenww.wumpus.commentary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentaryServiceTest {
    @Mock
    private CommentaryGateway commentaryGateway;

    @InjectMocks
    private CommentaryService commentaryService;

    @Test
    void shouldDelegateToGatewayWithSanitizedRequest() {
        when(commentaryGateway.generateCommentary(any())).thenReturn("  hello   cave  ");
        when(commentaryGateway.isFallbackGateway()).thenReturn(false);

        CommentaryResponse result = commentaryService.createCommentary(new CommentaryRequest(
                "move",
                5,
                "safe",
                5,
                Arrays.asList(1, 2, null, 3),
                List.of("  draft nearby "),
                4,
                7,
                List.of(" moved ", "shot "),
                null
        ));

        ArgumentCaptor<CommentaryRequest> captor = ArgumentCaptor.forClass(CommentaryRequest.class);
        verify(commentaryGateway).generateCommentary(captor.capture());

        CommentaryRequest sanitized = captor.getValue();
        assertEquals("MOVE", sanitized.action());
        assertEquals("SAFE", sanitized.outcome());
        assertEquals(List.of(1, 2, 3), sanitized.adjacentRooms());
        assertEquals(List.of("draft nearby"), sanitized.hazardWarnings());
        assertEquals(List.of("moved", "shot"), sanitized.previousActionSummaries());
        assertEquals("hello cave", result.commentary());
        assertFalse(result.fallback());
    }

    @Test
    void shouldReturnFallbackWhenGatewayThrows() {
        when(commentaryGateway.generateCommentary(any())).thenThrow(new RuntimeException("boom"));

        CommentaryResponse result = commentaryService.createCommentary(new CommentaryRequest(
                "shoot",
                null,
                "shot_wumpus",
                9,
                List.of(8, 10, 11),
                List.of(),
                2,
                10,
                List.of(),
                null
        ));

        assertTrue(result.fallback());
        assertNotNull(result.commentary());
        assertFalse(result.commentary().isBlank());
    }

    @Test
    void shouldUseFallbackWhenGatewayReturnsBlank() {
        when(commentaryGateway.generateCommentary(any())).thenReturn("   ");

        CommentaryResponse result = commentaryService.createCommentary(new CommentaryRequest(
                "shoot",
                null,
                "shot_self",
                9,
                List.of(8, 10, 11),
                List.of(),
                0,
                10,
                List.of(),
                null
        ));

        assertTrue(result.fallback());
        assertEquals("You have achieved the rare self-inflicted tactical defeat.", result.commentary());
    }

    @Test
    void shouldHandleNullRequest() {
        when(commentaryGateway.generateCommentary(any())).thenReturn("narration");
        when(commentaryGateway.isFallbackGateway()).thenReturn(false);

        CommentaryResponse result = commentaryService.createCommentary(null);

        ArgumentCaptor<CommentaryRequest> captor = ArgumentCaptor.forClass(CommentaryRequest.class);
        verify(commentaryGateway).generateCommentary(captor.capture());

        CommentaryRequest sanitized = captor.getValue();
        assertEquals("UNKNOWN", sanitized.action());
        assertEquals("UNKNOWN", sanitized.outcome());
        assertEquals(List.of(), sanitized.adjacentRooms());
        assertEquals(List.of(), sanitized.hazardWarnings());
        assertEquals("narration", result.commentary());
        assertFalse(result.fallback());
    }
}
