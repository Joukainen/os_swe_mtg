package org.osswe.mtg.engine;

import java.util.UUID;

/** Deterministic domain event; transport/persistence layers add wall-clock metadata. */
public record EngineEvent(EventType type, UUID playerId, UUID cardInstanceId) {
    public EngineEvent {
        if (type == null) throw new IllegalArgumentException("Event type is required");
    }
    public static EngineEvent of(EventType type, UUID playerId, UUID cardInstanceId) {
        return new EngineEvent(type, playerId, cardInstanceId);
    }
    public enum EventType { CARD_DRAWN, DRAW_FAILED_FROM_EMPTY_LIBRARY, LAND_PLAYED }
}
