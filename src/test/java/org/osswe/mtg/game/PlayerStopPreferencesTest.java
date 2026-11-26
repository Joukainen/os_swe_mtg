package org.osswe.mtg.game;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStopPreferencesTest {
    @Test
    void upkeepStopForOpponentDoesNotCreatePriority() {
        PlayerStopPreferences preferences = new PlayerStopPreferences(Set.of(), Set.of(GameStep.UPKEEP), false);
        assertTrue(preferences.wantsStop(false, GameStep.UPKEEP));
        assertFalse(preferences.wantsStop(true, GameStep.UPKEEP));
        assertFalse(preferences.holdPriorityAfterAction());
    }
}
