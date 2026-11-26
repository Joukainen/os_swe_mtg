package org.osswe.mtg.game;

import org.junit.jupiter.api.Test;
import org.osswe.mtg.card.CardDefinition;
import org.osswe.mtg.card.ManaCost;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    @Test
    void refusesCardDuplicatedAcrossZoneIndexes() {
        PlayerId one = PlayerId.create();
        PlayerId two = PlayerId.create();
        UUID definitionId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CardDefinition definition = new CardDefinition(definitionId, "Island", ManaCost.zero(),
                "Basic Land — Island", "", Set.of(), null, null);
        CardInstance card = new CardInstance(cardId, definitionId, one, one, Zone.HAND, false, 0, Map.of());
        PlayerState p1 = new PlayerState(one, "A", 20, ManaPool.empty(), List.of(), List.of(cardId),
                List.of(cardId), List.of(), List.of(), 0);
        PlayerState p2 = PlayerState.empty(two, "B");
        assertThrows(IllegalArgumentException.class, () -> new GameState(UUID.randomUUID(), 0, 1, one,
                GameStep.PRECOMBAT_MAIN, GameStatus.ACTIVE, Map.of(one, p1, two, p2),
                Map.of(definitionId, definition), Map.of(cardId, card), List.of(), PriorityState.forPlayer(one)));
    }
}
