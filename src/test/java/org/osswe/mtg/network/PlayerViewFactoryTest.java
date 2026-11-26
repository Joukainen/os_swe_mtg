package org.osswe.mtg.network;

import org.junit.jupiter.api.Test;
import org.osswe.mtg.card.CardDefinition;
import org.osswe.mtg.card.ManaCost;
import org.osswe.mtg.game.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerViewFactoryTest {
    @Test
    void opponentReceivesHandAndLibraryCountsButNotHiddenCardIdentities() {
        PlayerId a = PlayerId.create();
        PlayerId b = PlayerId.create();
        UUID defId = UUID.randomUUID();
        UUID aHandId = UUID.randomUUID();
        UUID bHandId = UUID.randomUUID();
        UUID bLibraryId = UUID.randomUUID();
        CardDefinition def = new CardDefinition(defId, "Ancestral Recall", ManaCost.parse("{U}"),
                "Instant", "Target player draws three cards.", Set.of("U"), null, null);
        Map<UUID, CardInstance> cards = Map.of(
                aHandId, new CardInstance(aHandId, defId, a, a, Zone.HAND, false, 0, Map.of()),
                bHandId, new CardInstance(bHandId, defId, b, b, Zone.HAND, false, 0, Map.of()),
                bLibraryId, new CardInstance(bLibraryId, defId, b, b, Zone.LIBRARY, false, 0, Map.of()));
        PlayerState p1 = new PlayerState(a, "Alice", 20, ManaPool.empty(), List.of(), List.of(aHandId),
                List.of(), List.of(), List.of(), 0);
        PlayerState p2 = new PlayerState(b, "Bob", 20, ManaPool.empty(), List.of(bLibraryId), List.of(bHandId),
                List.of(), List.of(), List.of(), 0);
        GameState state = new GameState(UUID.randomUUID(), 0, 1, a, GameStep.UPKEEP, GameStatus.ACTIVE,
                Map.of(a, p1, b, p2), Map.of(defId, def), cards, List.of(), PriorityState.forPlayer(a));
        PlayerView view = new PlayerViewFactory().create(state, a);
        assertEquals(List.of(aHandId), view.ownHand().stream().map(VisibleCard::instanceId).toList());
        assertEquals(1, view.players().get(b).handSize());
        assertEquals(1, view.players().get(b).librarySize());
        assertFalse(view.ownHand().stream().anyMatch(card -> card.instanceId().equals(bHandId)));
        assertFalse(view.ownHand().stream().anyMatch(card -> card.instanceId().equals(bLibraryId)));
    }
}
