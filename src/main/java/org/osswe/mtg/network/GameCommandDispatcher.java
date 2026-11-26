package org.osswe.mtg.network;

import org.springframework.stereotype.Component;

@Component
public class GameCommandDispatcher {
    /** Protocol smoke test only. Game-changing commands are rejected until rules handlers exist. */
    public ServerMessage dispatch(String authenticatedPlayer, ClientCommand command) {
        if (command == null || command.commandId() == null || command.type() == null || command.type().isBlank()) {
            return ServerMessage.error(command == null ? null : command.commandId(),
                    "INVALID_COMMAND", "Command type and commandId are required");
        }
        if (authenticatedPlayer == null || authenticatedPlayer.isBlank()) {
            return ServerMessage.error(command.commandId(), "UNAUTHENTICATED", "An authenticated player session is required");
        }
        if ("PING".equals(command.type())) return ServerMessage.pong(command.commandId());
        return ServerMessage.error(command.commandId(), "COMMAND_NOT_IMPLEMENTED",
                "The game command is not enabled in this architecture foundation");
    }
}
