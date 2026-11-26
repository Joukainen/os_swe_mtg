package org.osswe.mtg.engine;

import org.osswe.mtg.card.CardDefinition;
import org.osswe.mtg.game.CardInstance;
import org.osswe.mtg.game.GameState;
import org.osswe.mtg.game.GameStep;
import org.osswe.mtg.game.GameStatus;
import org.osswe.mtg.game.PlayerId;
import org.osswe.mtg.game.PlayerState;
import org.osswe.mtg.game.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Pure state-transition entry point. No WebSocket or persistence logic belongs here. */
public class GameEngine {
    /**
     * Performs the normal draw for turn as a turn-based action.
     * The emitted CARD_DRAWN event must be processed by the trigger checkpoint
     * before priority is granted. Trigger collection is not implemented yet.
     */
    public EngineResult drawForTurn(GameState state, PlayerId actor) {
        if (state.status() != GameStatus.ACTIVE) throw new GameRuleViolation("The game is not active");
        if (!state.activePlayerId().equals(actor)) throw new GameRuleViolation("Only the active player performs the turn draw");
        if (state.step() != GameStep.DRAW) throw new GameRuleViolation("The normal turn draw only occurs in the DRAW step");
        if (state.priority().isOpen()) throw new GameRuleViolation("The normal turn draw occurs before priority is given");
        if (state.normalDrawPerformedThisTurn()) throw new GameRuleViolation("The normal turn draw was already performed");

        PlayerState player = requirePlayer(state, actor);
        if (player.library().isEmpty()) {
            EngineEvent event = EngineEvent.of(EngineEvent.EventType.DRAW_FAILED_FROM_EMPTY_LIBRARY, actor.value(), null);
            return new EngineResult(state.afterNormalDraw(state.players(), state.cardInstances()), List.of(event));
        }

        UUID cardId = player.library().getFirst();
        CardInstance card = state.cardInstances().get(cardId);
        if (card.zone() != Zone.LIBRARY || !card.ownerId().equals(actor)) {
            throw new IllegalStateException("Library index is inconsistent with card instance");
        }
        List<UUID> library = new ArrayList<>(player.library());
        library.removeFirst();
        List<UUID> hand = new ArrayList<>(player.hand());
        hand.add(cardId);
        Map<PlayerId, PlayerState> players = new HashMap<>(state.players());
        players.put(actor, player.withZones(library, hand, player.battlefield(), player.graveyard(),
                player.exile(), player.landPlaysThisTurn()));
        Map<UUID, CardInstance> cards = new HashMap<>(state.cardInstances());
        cards.put(cardId, card.moveTo(Zone.HAND, actor));
        GameState next = state.afterNormalDraw(players, cards);
        EngineEvent event = EngineEvent.of(EngineEvent.EventType.CARD_DRAWN, actor.value(), cardId);
        return new EngineResult(next, List.of(event));
    }

    /** Playing a land is a special action: it bypasses the stack and keeps priority. */
    public EngineResult playLand(GameState state, PlayerId actor, UUID cardInstanceId) {
        if (state.status() != GameStatus.ACTIVE) throw new GameRuleViolation("The game is not active");
        if (!state.activePlayerId().equals(actor)) throw new GameRuleViolation("Only the active player may play a land");
        if (state.priority().playerWithPriority().filter(actor::equals).isEmpty()) {
            throw new GameRuleViolation("The player must have priority to play a land");
        }
        if (state.step() != GameStep.PRECOMBAT_MAIN && state.step() != GameStep.POSTCOMBAT_MAIN) {
            throw new GameRuleViolation("A land may only be played in a main phase");
        }
        if (!state.stack().isEmpty()) throw new GameRuleViolation("A land may only be played while the stack is empty");

        PlayerState player = requirePlayer(state, actor);
        if (player.landPlaysThisTurn() >= 1) throw new GameRuleViolation("The player has already played a land this turn");
        if (!player.hand().contains(cardInstanceId)) throw new GameRuleViolation("The land must be in the player's hand");
        CardInstance card = state.cardInstances().get(cardInstanceId);
        if (card == null || card.zone() != Zone.HAND || !card.ownerId().equals(actor)) {
            throw new GameRuleViolation("Card instance is not in the acting player's hand");
        }
        CardDefinition definition = state.cardDefinitions().get(card.cardDefinitionId());
        if (definition == null || !definition.isLand()) {
            throw new GameRuleViolation("Only a land card can be played as a land");
        }

        List<UUID> hand = new ArrayList<>(player.hand());
        hand.remove(cardInstanceId);
        List<UUID> battlefield = new ArrayList<>(player.battlefield());
        battlefield.add(cardInstanceId);
        PlayerState updatedPlayer = player.withZones(player.library(), hand, battlefield,
                player.graveyard(), player.exile(), player.landPlaysThisTurn() + 1);
        Map<PlayerId, PlayerState> players = new HashMap<>(state.players());
        players.put(actor, updatedPlayer);
        Map<UUID, CardInstance> cards = new HashMap<>(state.cardInstances());
        cards.put(cardInstanceId, card.moveTo(Zone.BATTLEFIELD, actor));
        GameState next = state.next(players, cards, state.stack(), state.step(), state.priority());
        EngineEvent event = EngineEvent.of(EngineEvent.EventType.LAND_PLAYED, actor.value(), cardInstanceId);
        return new EngineResult(next, List.of(event));
    }

    private PlayerState requirePlayer(GameState state, PlayerId id) {
        PlayerState player = state.players().get(id);
        if (player == null) throw new GameRuleViolation("Player does not belong to this game");
        return player;
    }
}
