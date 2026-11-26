package org.osswe.mtg.card;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManaCostTest {
    @Test
    void parsesColoredAndGenericMana() {
        ManaCost cost = ManaCost.parse("{3}{B}{B}");
        assertEquals(3, cost.generic());
        assertEquals(2, cost.black());
        assertEquals(5, cost.manaValue());
    }

    @Test
    void xHasZeroManaValueOutsideStackAndChosenValueOnStack() {
        ManaCost cost = ManaCost.parse("{X}{R}");
        assertEquals(1, cost.manaValue());
        assertEquals(6, cost.manaValueOnStack(5));
    }

    @Test
    void rejectsMalformedOrUnsupportedSymbols() {
        assertThrows(IllegalArgumentException.class, () -> ManaCost.parse("{2}{B"));
        assertThrows(IllegalArgumentException.class, () -> ManaCost.parse("{W/U}"));
    }
}
