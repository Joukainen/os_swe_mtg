package org.osswe.mtg.card;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Oracle-level logical card data; not a physical printing or in-game copy. */
public record CardDefinition(
        UUID oracleId, String name, ManaCost manaCost, String typeLine, String oracleText,
        Set<String> colors, String power, String toughness) {

    public CardDefinition {
        Objects.requireNonNull(oracleId, "oracleId is required");
        name = requireText(name, "name");
        Objects.requireNonNull(manaCost, "manaCost is required");
        typeLine = typeLine == null ? "" : typeLine.trim();
        oracleText = oracleText == null ? "" : oracleText;
        colors = colors == null ? Set.of() : Set.copyOf(colors);
        power = normalizeCharacteristic(power);
        toughness = normalizeCharacteristic(toughness);
    }

    public boolean isLand() {
        return typeLine.matches("(?i).*\\bLand\\b.*");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return value.trim();
    }

    private static String normalizeCharacteristic(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
