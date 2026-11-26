package org.osswe.mtg.network;

import org.osswe.mtg.game.CardInstance;
import org.osswe.mtg.game.GameState;
import org.osswe.mtg.game.PlayerId;
import org.osswe.mtg.game.PlayerState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PlayerViewFactory {
    public PlayerView create(GameState state, PlayerId viewerId) {
        if (!state.players().containsKey(viewerId)) {
            throw new IllegalArgumentException("Viewer is not a player in this game");
        }
        Map<PlayerId, PlayerSummary> summaries = state.players().values().stream()
                .collect(Collectors.toUnmodifiableMap(PlayerState::id, player -> new PlayerSummary(
                        player.id(), player.displayName(), player.lifeTotal(),
                        player.hand().size(), player.library().size())));
        PlayerState viewer = state.players().get(viewerId);
        List<VisibleCard> ownHand = visible(viewer.hand(), state);
        Map<PlayerId, List<VisibleCard>> battlefield = state.players().values().stream()
                .collect(Collectors.toUnmodifiableMap(PlayerState::id, player -> visible(player.battlefield(), state)));
        Map<PlayerId, List<VisibleCard>> graveyards = state.players().values().stream()
                .collect(Collectors.toUnmodifiableMap(PlayerState::id, player -> visible(player.graveyard(), state)));
        return new PlayerView(state.gameId(), state.revision(), viewerId, state.activePlayerId(),
                state.step(), summaries, ownHand, battlefield, graveyards, state.stack());
    }

    private List<VisibleCard> visible(List<java.util.UUID> ids, GameState state) {
        return ids.stream().map(id -> toVisible(state.cardInstances().get(id), state)).toList();
    }

    private VisibleCard toVisible(CardInstance instance, GameState state) {
        var definition = state.cardDefinitions().get(instance.cardDefinitionId());
        return new VisibleCard(instance.instanceId(), definition.oracleId(), definition.name(),
                definition.typeLine(), instance.tapped(), instance.markedDamage());
    }
}
