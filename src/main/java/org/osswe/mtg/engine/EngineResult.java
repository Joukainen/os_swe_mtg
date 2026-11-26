package org.osswe.mtg.engine;

import org.osswe.mtg.game.GameState;
import java.util.List;

public record EngineResult(GameState state, List<EngineEvent> events) {
    public EngineResult {
        if (state == null) throw new IllegalArgumentException("Result state is required");
        events = List.copyOf(events == null ? List.of() : events);
    }
}
