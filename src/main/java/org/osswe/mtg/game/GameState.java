package org.osswe.mtg.game;

import org.osswe.mtg.card.CardDefinition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Immutable aggregate snapshot. Zone lists are indexes; CardInstance.zone is authoritative. */
public record GameState(
        UUID gameId, long revision, int turnNumber, PlayerId activePlayerId,
        GameStep step, GameStatus status, Map<PlayerId, PlayerState> players,
        Map<UUID, CardDefinition> cardDefinitions, Map<UUID, CardInstance> cardInstances,
        List<StackObject> stack, PriorityState priority, boolean normalDrawPerformedThisTurn) {

    public GameState {
        Objects.requireNonNull(gameId, "gameId is required");
        if (revision < 0 || turnNumber < 1) throw new IllegalArgumentException("Invalid revision or turn number");
        Objects.requireNonNull(activePlayerId, "activePlayerId is required");
        Objects.requireNonNull(step, "step is required");
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(priority, "priority is required");
        players = Map.copyOf(players == null ? Map.of() : players);
        cardDefinitions = Map.copyOf(cardDefinitions == null ? Map.of() : cardDefinitions);
        cardInstances = Map.copyOf(cardInstances == null ? Map.of() : cardInstances);
        stack = List.copyOf(stack == null ? List.of() : stack);
        if (players.size() != 2) throw new IllegalArgumentException("A game must contain exactly two players");
        if (!players.containsKey(activePlayerId)) throw new IllegalArgumentException("Active player is not in this game");
        for (Map.Entry<PlayerId, PlayerState> entry : players.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().id())) {
                throw new IllegalArgumentException("Player map key does not match player id");
            }
        }
        if (priority.playerWithPriority().isPresent()
                && !players.containsKey(priority.playerWithPriority().orElseThrow())) {
            throw new IllegalArgumentException("Priority holder is not in this game");
        }
        for (Map.Entry<UUID, CardInstance> entry : cardInstances.entrySet()) {
            CardInstance card = entry.getValue();
            if (!entry.getKey().equals(card.instanceId())) {
                throw new IllegalArgumentException("Card map key does not match instance id");
            }
            if (!players.containsKey(card.ownerId()) || !players.containsKey(card.controllerId())) {
                throw new IllegalArgumentException("Card owner and controller must belong to the game");
            }
            if (!cardDefinitions.containsKey(card.cardDefinitionId())) {
                throw new IllegalArgumentException("Card instance references a missing card definition");
            }
        }
        validateZoneIndexes(players, cardInstances, stack);
    }

    /** Convenience constructor for a newly created game before the normal draw. */
    public GameState(UUID gameId, long revision, int turnNumber, PlayerId activePlayerId,
                     GameStep step, GameStatus status, Map<PlayerId, PlayerState> players,
                     Map<UUID, CardDefinition> cardDefinitions, Map<UUID, CardInstance> cardInstances,
                     List<StackObject> stack, PriorityState priority) {
        this(gameId, revision, turnNumber, activePlayerId, step, status, players,
                cardDefinitions, cardInstances, stack, priority, false);
    }

    public GameState next(Map<PlayerId, PlayerState> newPlayers, Map<UUID, CardInstance> newCardInstances,
                          List<StackObject> newStack, GameStep newStep, PriorityState newPriority) {
        return new GameState(gameId, revision + 1, turnNumber, activePlayerId, newStep,
                status, newPlayers, cardDefinitions, newCardInstances, newStack, newPriority,
                normalDrawPerformedThisTurn);
    }

    /** Records the normal draw action, including an unsuccessful attempt with an empty library. */
    public GameState afterNormalDraw(Map<PlayerId, PlayerState> newPlayers,
                                     Map<UUID, CardInstance> newCardInstances) {
        if (normalDrawPerformedThisTurn) throw new IllegalStateException("The normal draw was already performed this turn");
        return new GameState(gameId, revision + 1, turnNumber, activePlayerId, step, status,
                newPlayers, cardDefinitions, newCardInstances, stack, priority, true);
    }

    private static void validateZoneIndexes(Map<PlayerId, PlayerState> players,
                                            Map<UUID, CardInstance> cards,
                                            List<StackObject> stack) {
        Map<UUID, Zone> indexed = new HashMap<>();
        for (PlayerState player : players.values()) {
            index(player.library(), Zone.LIBRARY, player.id(), cards, indexed, false);
            index(player.hand(), Zone.HAND, player.id(), cards, indexed, false);
            index(player.battlefield(), Zone.BATTLEFIELD, player.id(), cards, indexed, true);
            index(player.graveyard(), Zone.GRAVEYARD, player.id(), cards, indexed, false);
            index(player.exile(), Zone.EXILE, player.id(), cards, indexed, false);
        }
        Set<UUID> stackCardIds = new HashSet<>();
        for (StackObject object : stack) {
            if (!players.containsKey(object.controllerId())) {
                throw new IllegalArgumentException("Stack object controller is not in this game");
            }
            if (object.sourceCardInstanceId() != null && !cards.containsKey(object.sourceCardInstanceId())) {
                throw new IllegalArgumentException("Stack object references an unknown source card instance");
            }
            if (object.kind() == StackObjectKind.SPELL) {
                if (!stackCardIds.add(object.cardInstanceId())) {
                    throw new IllegalArgumentException("A card instance cannot appear twice on the stack");
                }
                CardInstance card = cards.get(object.cardInstanceId());
                if (card == null || card.zone() != Zone.STACK) {
                    throw new IllegalArgumentException("Spell stack object must reference a card in the STACK zone");
                }
            }
        }
        for (CardInstance card : cards.values()) {
            if (card.zone() == Zone.STACK) {
                if (!stackCardIds.contains(card.instanceId())) {
                    throw new IllegalArgumentException("A card in STACK must have one spell stack object");
                }
                if (indexed.containsKey(card.instanceId())) {
                    throw new IllegalArgumentException("A stack card cannot also be indexed in a player zone");
                }
            } else if (indexed.get(card.instanceId()) != card.zone()) {
                throw new IllegalArgumentException("Card instance is missing from its matching zone index: " + card.instanceId());
            }
        }
        if (indexed.size() + stackCardIds.size() != cards.size()) {
            throw new IllegalArgumentException("Every card instance must occur in exactly one zone");
        }
    }

    private static void index(List<UUID> ids, Zone expectedZone, PlayerId playerId,
                              Map<UUID, CardInstance> cards, Map<UUID, Zone> indexed,
                              boolean byController) {
        for (UUID id : ids) {
            CardInstance card = cards.get(id);
            if (card == null) throw new IllegalArgumentException("Zone index references an unknown card instance");
            PlayerId expectedPlayer = byController ? card.controllerId() : card.ownerId();
            if (!playerId.equals(expectedPlayer) || card.zone() != expectedZone) {
                throw new IllegalArgumentException("Zone index does not match card owner/controller or zone");
            }
            if (indexed.putIfAbsent(id, expectedZone) != null) {
                throw new IllegalArgumentException("Card instance appears in more than one player zone");
            }
        }
    }
}
