package org.osswe.mtg.network;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocket
public class GameWebSocketConfiguration implements WebSocketConfigurer {
    private final GameWebSocketHandler handler;
    private final List<String> allowedOrigins;

    public GameWebSocketConfiguration(
            GameWebSocketHandler handler,
            @Value("${os-swe.websocket.allowed-origins:http://localhost:8080,http://127.0.0.1:8080}") String origins) {
        this.handler = handler;
        this.allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim).filter(value -> !value.isEmpty()).toList();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/games")
                .setAllowedOrigins(allowedOrigins.toArray(String[]::new));
    }
}
