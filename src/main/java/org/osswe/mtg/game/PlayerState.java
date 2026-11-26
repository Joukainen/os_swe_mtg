package org.osswe.mtg.game;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Player-owned zones plus battlefield permanents currently controlled by this player. */
public record PlayerState(
        PlayerId id, String displayName, int lifeTotal, ManaPool manaPool,
        List<UUID> library, List<UUID> hand, List<UUID> battlefield,
        List<UUID> graveyard, List<UUID> exile, int landPlaysThisTurn) {

    public PlayerState {
        Objects.requireNonNull(id, "id is required");
        if (displayName == null || displayName.isBlank()) throw new IllegalArgumentException("displayName is required");
        // Negative life totals are valid game state until the next state-based-action check.
        manaPool = manaPool == null ? ManaPool.empty() : manaPool;
        library = List.copyOf(library == null ? List.of() : library);
        hand = List.copyOf(hand == null ? List.of() : hand);
        battlefield = List.copyOf(battlefield == null ? List.of() : battlefield);
        graveyard = List.copyOf(graveyard == null ? List.of() : graveyard);
        exile = List.copyOf(exile == null ? List.of() : exile);
        if (landPlaysThisTurn < 0) throw new IllegalArgumentException("landPlaysThisTurn cannot be negative");
    }

    public static PlayerState empty(PlayerId id, String name) {
        return new PlayerState(id, name, 20, ManaPool.empty(), List.of(), List.of(),
                List.of(), List.of(), List.of(), 0);
    }

    public List<UUID> cardsIn(Zone zone) {
        return switch (zone) {
            case LIBRARY -> library;
            case HAND -> hand;
            case BATTLEFIELD -> battlefield;
            case GRAVEYARD -> graveyard;
            case EXILE -> exile;
            case STACK -> List.of();
        };
    }

    public PlayerState withZones(List<UUID> newLibrary, List<UUID> newHand, List<UUID> newBattlefield,
                                 List<UUID> newGraveyard, List<UUID> newExile, int newLandPlaysThisTurn) {
        return new PlayerState(id, displayName, lifeTotal, manaPool, newLibrary, newHand, newBattlefield,
                newGraveyard, newExile, newLandPlaysThisTurn);
    }
}
