package org.osswe.mtg.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStateTest {
    @Test
    void permitsNegativeLifeUntilStateBasedActionsAreChecked() {
        PlayerState state = new PlayerState(PlayerId.create(), "Player", -2, ManaPool.empty(),
                java.util.List.of(), java.util.List.of(), java.util.List.of(), java.util.List.of(),
                java.util.List.of(), 0);
        assertEquals(-2, state.lifeTotal());
    }
}
