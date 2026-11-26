package org.osswe.mtg.network;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final JsonMapper jsonMapper;
    private final GameCommandDispatcher dispatcher;

    public GameWebSocketHandler(JsonMapper jsonMapper, GameCommandDispatcher dispatcher) {
        this.jsonMapper = jsonMapper;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (session.getPrincipal() == null) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Authenticated session required"));
            return;
        }
        try {
            ClientCommand command = jsonMapper.readValue(message.getPayload(), ClientCommand.class);
            ServerMessage response = dispatcher.dispatch(session.getPrincipal().getName(), command);
            session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(response)));
        } catch (JacksonException exception) {
            ServerMessage response = ServerMessage.error(null, "INVALID_JSON",
                    "Message must be a valid command envelope");
            session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(response)));
        }
    }
}
