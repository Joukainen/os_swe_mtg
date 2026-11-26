package org.osswe.mtg.network;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;
import java.util.UUID;

/** Client command envelope. Player identity is intentionally not a payload field. */
public record ClientCommand(
        @NotBlank String type,
        @NotNull UUID commandId,
        UUID gameId,
        Long expectedRevision,
        JsonNode payload) {
}
