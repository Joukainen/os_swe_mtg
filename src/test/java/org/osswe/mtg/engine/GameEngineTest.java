package org.osswe.mtg.engine;

import org.junit.jupiter.api.Test;
import org.osswe.mtg.card.CardDefinition;
import org.osswe.mtg.card.ManaCost;
import org.osswe.mtg.game.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {
    private final GameEngine engine = new GameEngine();

    @Test
    void landPlayIsImmediateSpecialActionAndDoesNotUseStack() {
        Fixture fixture = fixture(true, GameStep.PRECOMBAT_MAIN);
        GameState state = withPriority(fixture.state, fixture.playerOne);
        EngineResult result = engine.playLand(state, fixture.playerOne, fixture.cardId);
        assertEquals(Zone.BATTLEFIELD, result.state().cardInstances().get(fixture.cardId).zone());
        assertTrue(result.state().players().get(fixture.playerOne).battlefield().contains(fixture.cardId));
        assertTrue(result.state().stack().isEmpty());
        assertEquals(1, result.state().players().get(fixture.playerOne).landPlaysThisTurn());
        assertEquals(EngineEvent.EventType.LAND_PLAYED, result.events().getFirst().type());
    }

    @Test
    void nonlandCannotUseLandPlayAction() {
        Fixture fixture = fixture(false, GameStep.PRECOMBAT_MAIN);
        GameState state = withPriority(fixture.state, fixture.playerOne);
        assertThrows(GameRuleViolation.class, () -> engine.playLand(state, fixture.playerOne, fixture.cardId));
    }

    @Test
    void landPlayRequiresEmptyStack() {
        Fixture fixture = fixture(true, GameStep.PRECOMBAT_MAIN);
        StackObject trigger = new StackObject(UUID.randomUUID(), StackObjectKind.TRIGGERED_ABILITY,
                fixture.playerOne, null, null, "test-trigger", List.of());
        GameState state = new GameState(fixture.state.gameId(), fixture.state.revision(), 1,
                fixture.playerOne, GameStep.PRECOMBAT_MAIN, GameStatus.ACTIVE,
                fixture.state.players(), fixture.state.cardDefinitions(), fixture.state.cardInstances(),
                List.of(trigger), PriorityState.forPlayer(fixture.playerOne));
        assertThrows(GameRuleViolation.class, () -> engine.playLand(state, fixture.playerOne, fixture.cardId));
    }

    @Test
    void normalDrawMovesTopCardBeforePriorityAndCanOnlyHappenOnce() {
        Fixture fixture = fixture(false, GameStep.DRAW);
        EngineResult result = engine.drawForTurn(fixture.state, fixture.playerOne);
        assertTrue(result.state().players().get(fixture.playerOne).library().isEmpty());
        assertTrue(result.state().players().get(fixture.playerOne).hand().contains(fixture.cardId));
        assertEquals(Zone.HAND, result.state().cardInstances().get(fixture.cardId).zone());
        assertTrue(result.state().normalDrawPerformedThisTurn());
        assertFalse(result.state().priority().isOpen());
        assertEquals(EngineEvent.EventType.CARD_DRAWN, result.events().getFirst().type());
        assertThrows(GameRuleViolation.class, () -> engine.drawForTurn(result.state(), fixture.playerOne));
    }

    @Test
    void failedDrawFromEmptyLibraryStillConsumesNormalDrawAction() {
        PlayerId one = PlayerId.create();
        PlayerId two = PlayerId.create();
        PlayerState p1 = PlayerState.empty(one, "A");
        PlayerState p2 = PlayerState.empty(two, "B");
        GameState state = new GameState(UUID.randomUUID(), 0, 1, one, GameStep.DRAW,
                GameStatus.ACTIVE, Map.of(one, p1, two, p2), Map.of(), Map.of(), List.of(), PriorityState.closed());
        EngineResult result = engine.drawForTurn(state, one);
        assertEquals(EngineEvent.EventType.DRAW_FAILED_FROM_EMPTY_LIBRARY, result.events().getFirst().type());
        assertTrue(result.state().normalDrawPerformedThisTurn());
        assertFalse(result.state().priority().isOpen());
        assertThrows(GameRuleViolation.class, () -> engine.drawForTurn(result.state(), one));
    }

    private GameState withPriority(GameState state, PlayerId player) {
        return new GameState(state.gameId(), state.revision(), state.turnNumber(), state.activePlayerId(),
                state.step(), state.status(), state.players(), state.cardDefinitions(), state.cardInstances(),
                state.stack(), PriorityState.forPlayer(player));
    }

    private Fixture fixture(boolean land, GameStep step) {
        PlayerId one = PlayerId.create();
        PlayerId two = PlayerId.create();
        UUID definitionId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CardDefinition definition = new CardDefinition(definitionId, land ? "Island" : "Grizzly Bears",
                ManaCost.zero(), land ? "Basic Land — Island" : "Creature — Bear", "", Set.of(),
                land ? null : "2", land ? null : "2");
        boolean inLibrary = step == GameStep.DRAW;
        CardInstance instance = new CardInstance(cardId, definitionId, one, one,
                inLibrary ? Zone.LIBRARY : Zone.HAND, false, 0, Map.of());
        PlayerState p1 = new PlayerState(one, "A", 20, ManaPool.empty(),
                inLibrary ? List.of(cardId) : List.of(), inLibrary ? List.of() : List.of(cardId),
                List.of(), List.of(), List.of(), 0);
        PlayerState p2 = PlayerState.empty(two, "B");
        GameState state = new GameState(UUID.randomUUID(), 0, 1, one, step, GameStatus.ACTIVE,
                Map.of(one, p1, two, p2), Map.of(definitionId, definition), Map.of(cardId, instance),
                List.of(), PriorityState.closed());
        return new Fixture(state, one, cardId);
    }

    private record Fixture(GameState state, PlayerId playerOne, UUID cardId) {}
}
