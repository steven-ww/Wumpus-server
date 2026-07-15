package com.stevenww.wumpus.commentary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.Locale;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CommentaryService {
    private static final Logger LOG = Logger.getLogger(CommentaryService.class);
    private static final int MAX_ADJACENT_ROOMS = 6;
    private static final int MAX_WARNINGS = 6;
    private static final int MAX_HISTORY = 3;
    private static final int MAX_TEXT_ITEM_LENGTH = 100;
    private static final int MAX_COMMENTARY_LENGTH = 240;

    private final CommentaryGateway commentaryGateway;

    @Inject
    public CommentaryService(@Named("selectedCommentaryGateway") CommentaryGateway commentaryGateway) {
        this.commentaryGateway = commentaryGateway;
    }
    @Timeout(value = 4500, unit = ChronoUnit.MILLIS)
    @Fallback(fallbackMethod = "fallbackCommentary")

    public CommentaryResponse createCommentary(CommentaryRequest request) {
        CommentaryRequest safeRequest = sanitizeRequest(request);
        try {
            String generated = normalizeText(commentaryGateway.generateCommentary(safeRequest), MAX_COMMENTARY_LENGTH);
            if (generated.isBlank()) {
                return new CommentaryResponse(defaultFallbackCommentary(safeRequest), true);
            }
            return new CommentaryResponse(generated, commentaryGateway.isFallbackGateway());
        } catch (RuntimeException ex) {
            LOG.debug("Commentary generation failed. Returning deterministic fallback.", ex);
            return new CommentaryResponse(defaultFallbackCommentary(safeRequest), true);
        }
    }
    public CommentaryResponse fallbackCommentary(CommentaryRequest request) {
        return new CommentaryResponse(defaultFallbackCommentary(sanitizeRequest(request)), true);
    }

    public CommentaryResponse rateLimitedCommentary(CommentaryRequest request) {
        return new CommentaryResponse(
                "The narrator is catching their breath between disasters.",
                true
        );
    }

    private CommentaryRequest sanitizeRequest(CommentaryRequest request) {
        if (request == null) {
            return new CommentaryRequest(
                    "UNKNOWN",
                    "UNKNOWN",
                    null,
                    List.of(),
                    null,
                    "UNKNOWN",
                    null,
                    List.of(),
                    List.of(),
                    null,
                    null,
                    List.of(),
                    null
            );
        }

        return new CommentaryRequest(
                normalizeToken(request.action()),
                normalizeToken(request.actionIntent()),
                request.intendedTargetRoom(),
                sanitizeIntegerList(request.nominatedPath(), 5),
                request.targetRoom(),
                normalizeToken(request.outcome()),
                request.playerRoom(),
                sanitizeIntegerList(request.adjacentRooms(), MAX_ADJACENT_ROOMS),
                sanitizeTextList(request.hazardWarnings(), MAX_WARNINGS, MAX_TEXT_ITEM_LENGTH),
                request.arrowsRemaining(),
                request.movesTaken(),
                sanitizeTextList(request.previousActionSummaries(), MAX_HISTORY, MAX_TEXT_ITEM_LENGTH),
                sanitizeHiddenState(request.debugHiddenState())
        );
    }

    private CommentaryRequest.DebugHiddenState sanitizeHiddenState(CommentaryRequest.DebugHiddenState hiddenState) {
        if (hiddenState == null) {
            return null;
        }
        return new CommentaryRequest.DebugHiddenState(
                hiddenState.wumpusRoom(),
                sanitizeIntegerList(hiddenState.pits(), 6),
                sanitizeIntegerList(hiddenState.bats(), 6)
        );
    }

    private List<Integer> sanitizeIntegerList(List<Integer> values, int maxItems) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null)
                .limit(maxItems)
                .toList();
    }

    private List<String> sanitizeTextList(List<String> values, int maxItems, int maxItemLength) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null)
                .map(value -> normalizeText(value, maxItemLength))
                .filter(value -> !value.isBlank())
                .limit(maxItems)
                .toList();
    }

    private String normalizeToken(String value) {
        if (value == null || value.isBlank()) {
            return "UNKNOWN";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String compact = text.trim().replaceAll("\\s+", " ");
        if (compact.length() <= maxLength) {
            return compact;
        }
        return compact.substring(0, maxLength).trim();
    }

    private String defaultFallbackCommentary(CommentaryRequest request) {
        if ("SHOT_WUMPUS".equals(request.outcome())) {
            return "That worked. Nobody is more surprised than the cave.";
        }
        if ("SHOT_SELF".equals(request.outcome())) {
            return "You have achieved the rare self-inflicted tactical defeat.";
        }
        return "The cave acknowledged your move and withheld applause.";
    }
}
