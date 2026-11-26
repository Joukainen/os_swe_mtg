package org.osswe.mtg.network;

import org.osswe.mtg.game.GameStep;
import org.osswe.mtg.game.PlayerId;
import org.osswe.mtg.game.StackObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Player-specific projection. Opponent hand and library card identities are never included. */
public record PlayerView(
        UUID gameId, long revision, PlayerId viewerId, PlayerId activePlayerId,
        GameStep step, Map<PlayerId, PlayerSummary> players, List<VisibleCard> ownHand,
        Map<PlayerId, List<VisibleCard>> battlefieldByController,
        Map<PlayerId, List<VisibleCard>> graveyardByOwner, List<StackObject> publicStack) {

    public PlayerView {
        players = Map.copyOf(players);
        ownHand = List.copyOf(ownHand);
        battlefieldByController = immutableLists(battlefieldByController);
        graveyardByOwner = immutableLists(graveyardByOwner);
        publicStack = List.copyOf(publicStack);
    }

    private static Map<PlayerId, List<VisibleCard>> immutableLists(Map<PlayerId, List<VisibleCard>> source) {
        return source.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
    }
}
