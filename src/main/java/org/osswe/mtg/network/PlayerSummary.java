package org.osswe.mtg.network;

import org.osswe.mtg.game.PlayerId;

public record PlayerSummary(PlayerId playerId, String displayName, int lifeTotal,
                            int handSize, int librarySize) {
}
