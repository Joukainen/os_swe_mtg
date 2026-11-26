package org.osswe.mtg.game;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** One physical card copy during one game. State changes replace this value. */
public record CardInstance(
        UUID instanceId, UUID cardDefinitionId, PlayerId ownerId, PlayerId controllerId,
        Zone zone, boolean tapped, int markedDamage, Map<String, Integer> counters) {

    public CardInstance {
        Objects.requireNonNull(instanceId, "instanceId is required");
        Objects.requireNonNull(cardDefinitionId, "cardDefinitionId is required");
        Objects.requireNonNull(ownerId, "ownerId is required");
        Objects.requireNonNull(controllerId, "controllerId is required");
        Objects.requireNonNull(zone, "zone is required");
        if (markedDamage < 0) throw new IllegalArgumentException("Marked damage cannot be negative");
        counters = counters == null ? Map.of() : Map.copyOf(counters);
        if (counters.values().stream().anyMatch(value -> value == null || value < 0)) {
            throw new IllegalArgumentException("Counter values cannot be negative or null");
        }
    }

    public CardInstance moveTo(Zone destination, PlayerId newController) {
        Objects.requireNonNull(destination, "destination is required");
        PlayerId controller = newController == null ? controllerId : newController;
        boolean remainsOnBattlefield = zone == Zone.BATTLEFIELD && destination == Zone.BATTLEFIELD;
        return new CardInstance(instanceId, cardDefinitionId, ownerId, controller, destination,
                remainsOnBattlefield && tapped, remainsOnBattlefield ? markedDamage : 0,
                remainsOnBattlefield ? counters : Map.of());
    }
}
