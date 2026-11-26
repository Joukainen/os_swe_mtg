package org.osswe.mtg.card;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CardPrintingTest {
    @Test
    void acceptsOnlyTheEightPermittedSets() {
        assertDoesNotThrow(() -> new CardPrinting(UUID.randomUUID(), UUID.randomUUID(),
                "lea", "1", "", "Creature", null));
        assertThrows(IllegalArgumentException.class, () -> new CardPrinting(UUID.randomUUID(),
                UUID.randomUUID(), "m21", "1", "", "Creature", null));
    }
}
