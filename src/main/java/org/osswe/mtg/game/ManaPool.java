package org.osswe.mtg.game;

public record ManaPool(int white, int blue, int black, int red, int green, int colorless) {
    public ManaPool {
        if (white < 0 || blue < 0 || black < 0 || red < 0 || green < 0 || colorless < 0) {
            throw new IllegalArgumentException("Mana pool values cannot be negative");
        }
    }
    public static ManaPool empty() { return new ManaPool(0, 0, 0, 0, 0, 0); }
}
