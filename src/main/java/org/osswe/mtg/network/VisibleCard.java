package org.osswe.mtg.network;

import java.util.UUID;

/** A card representation safe to include in a player's authorized view. */
public record VisibleCard(
        UUID instanceId, UUID oracleId, String name, String typeLine, boolean tapped, int markedDamage) {
}
