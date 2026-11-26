package org.osswe.mtg.game;

import java.util.UUID;

public record PlayerId(UUID value) {
    public PlayerId {
        if (value == null) throw new IllegalArgumentException("Player id is required");
    }
    public static PlayerId create() { return new PlayerId(UUID.randomUUID()); }
}
