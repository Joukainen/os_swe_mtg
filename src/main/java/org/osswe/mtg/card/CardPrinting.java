package org.osswe.mtg.card;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** One physical/set printing of an Oracle-level card. */
public record CardPrinting(
        UUID scryfallId, UUID oracleId, String setCode, String collectorNumber,
        String printedText, String printedTypeLine, URI normalImageUri) {

    private static final Set<String> ALLOWED_SETS = Set.of("lea", "leb", "2ed", "arn", "atq", "3ed", "leg", "drk");

    public CardPrinting {
        Objects.requireNonNull(scryfallId, "scryfallId is required");
        Objects.requireNonNull(oracleId, "oracleId is required");
        setCode = Objects.requireNonNull(setCode, "setCode is required").toLowerCase(Locale.ROOT);
        if (!ALLOWED_SETS.contains(setCode)) {
            throw new IllegalArgumentException("Printing is outside the Alpha–The Dark card pool: " + setCode);
        }
        collectorNumber = collectorNumber == null ? "" : collectorNumber;
        printedText = printedText == null ? "" : printedText;
        printedTypeLine = printedTypeLine == null ? "" : printedTypeLine;
        if (normalImageUri != null && (normalImageUri.getScheme() == null
                || !Set.of("https", "http").contains(normalImageUri.getScheme().toLowerCase(Locale.ROOT)))) {
            throw new IllegalArgumentException("Image URI must use HTTP(S)");
        }
    }
}
