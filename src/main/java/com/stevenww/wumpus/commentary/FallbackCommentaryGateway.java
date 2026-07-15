package com.stevenww.wumpus.commentary;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Locale;

@ApplicationScoped
public class FallbackCommentaryGateway implements CommentaryGateway {
    @Override
    public String generateCommentary(CommentaryRequest request) {
        String outcome = normalizeToken(request.outcome());
        if ("SHOT_WUMPUS".equals(outcome)) {
            return "Against all odds, that arrow actually did what you wanted.";
        }
        if ("SHOT_SELF".equals(outcome)) {
            return "A daring tactic: turning yourself into your own target.";
        }
        if ("PIT_DEATH".equals(outcome)) {
            return "Gravity appreciates your commitment to direct problem solving.";
        }
        if ("BATS_RELOCATED".equals(outcome)) {
            return "The bats have updated your travel itinerary without consulting you.";
        }
        if ("WUMPUS_BUMPED".equals(outcome)) {
            return "You found the Wumpus at close range. It found you first.";
        }
        if ("OUT_OF_ARROWS".equals(outcome)) {
            return "Excellent. Now every bad decision is fully immersive.";
        }
        if ("SHOT_MISSED".equals(outcome)) {
            return "Another precision shot, if the target was empty cave air.";
        }

        String action = normalizeToken(request.action());
        if ("INVALID_ACTION".equals(action)) {
            return "That command had spirit, if not meaning.";
        }
        if ("MOVE".equals(action) && request.targetRoom() != null) {
            return "Room " + request.targetRoom() + " again? Predictability is a strategy, technically.";
        }
        if ("SHOOT".equals(action)) {
            return "You keep negotiating with danger at arrow-point.";
        }
        return "The cave heard you and remained politely unimpressed.";
    }

    @Override
    public boolean isFallbackGateway() {
        return true;
    }

    private String normalizeToken(String value) {
        if (value == null || value.isBlank()) {
            return "UNKNOWN";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
