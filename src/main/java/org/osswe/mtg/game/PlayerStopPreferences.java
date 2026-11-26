package org.osswe.mtg.game;

import java.util.Set;

/** Client-side automation preferences; these settings never grant rules priority. */
public record PlayerStopPreferences(
        Set<GameStep> ownTurnStops, Set<GameStep> opponentTurnStops, boolean holdPriorityAfterAction) {
    public PlayerStopPreferences {
        ownTurnStops = Set.copyOf(ownTurnStops == null ? Set.of() : ownTurnStops);
        opponentTurnStops = Set.copyOf(opponentTurnStops == null ? Set.of() : opponentTurnStops);
    }
    public boolean wantsStop(boolean viewerIsActivePlayer, GameStep step) {
        return (viewerIsActivePlayer ? ownTurnStops : opponentTurnStops).contains(step);
    }
    public static PlayerStopPreferences none() {
        return new PlayerStopPreferences(Set.of(), Set.of(), false);
    }
}
