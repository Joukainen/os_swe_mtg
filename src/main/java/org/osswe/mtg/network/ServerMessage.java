package org.osswe.mtg.network;

import tools.jackson.databind.JsonNode;
import java.util.UUID;

public record ServerMessage(
        String type, UUID commandId, UUID gameId, Long revision,
        String code, String message, JsonNode payload) {

    public static ServerMessage pong(UUID commandId) {
        return new ServerMessage("PONG", commandId, null, null, null, null, null);
    }

    public static ServerMessage error(UUID commandId, String code, String message) {
        return new ServerMessage("ERROR", commandId, null, null, code, message, null);
    }
}
