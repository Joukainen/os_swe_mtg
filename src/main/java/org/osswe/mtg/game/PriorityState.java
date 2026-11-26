package org.osswe.mtg.game;

import java.util.Optional;

/** Priority checkpoint state. Complete pass/resolution sequencing is a later rules slice. */
public record PriorityState(Optional<PlayerId> playerWithPriority, int consecutivePasses) {
    public PriorityState {
        playerWithPriority = playerWithPriority == null ? Optional.empty() : playerWithPriority;
        if (consecutivePasses < 0 || consecutivePasses > 1) {
            throw new IllegalArgumentException("consecutivePasses must be 0 or 1");
        }
        if (playerWithPriority.isEmpty() && consecutivePasses != 0) {
            throw new IllegalArgumentException("Closed priority cannot have pass history");
        }
    }
    public static PriorityState closed() { return new PriorityState(Optional.empty(), 0); }
    public static PriorityState forPlayer(PlayerId playerId) { return new PriorityState(Optional.of(playerId), 0); }
    public boolean isOpen() { return playerWithPriority.isPresent(); }
}
