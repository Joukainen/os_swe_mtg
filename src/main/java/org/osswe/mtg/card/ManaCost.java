package org.osswe.mtg.card;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parsed mana cost. X has zero mana value outside the stack. */
public record ManaCost(
        int generic, int white, int blue, int black, int red, int green, int colorless, int xSymbols) {

    private static final Pattern SYMBOL = Pattern.compile("\\{([^{}]+)}");

    public ManaCost {
        if (generic < 0 || white < 0 || blue < 0 || black < 0 || red < 0
                || green < 0 || colorless < 0 || xSymbols < 0) {
            throw new IllegalArgumentException("Mana-cost components cannot be negative");
        }
    }

    public static ManaCost zero() {
        return new ManaCost(0, 0, 0, 0, 0, 0, 0, 0);
    }

    /** Parses the coloured, colourless, generic and X symbols used in Alpha–The Dark. */
    public static ManaCost parse(String value) {
        if (value == null || value.isBlank()) return zero();
        String input = value.trim();
        Matcher matcher = SYMBOL.matcher(input);
        int end = 0, generic = 0, white = 0, blue = 0, black = 0, red = 0, green = 0, colorless = 0, xSymbols = 0;
        while (matcher.find()) {
            if (!input.substring(end, matcher.start()).isBlank()) {
                throw new IllegalArgumentException("Malformed mana cost: " + value);
            }
            String symbol = matcher.group(1).trim().toUpperCase(Locale.ROOT);
            switch (symbol) {
                case "W" -> white++;
                case "U" -> blue++;
                case "B" -> black++;
                case "R" -> red++;
                case "G" -> green++;
                case "C" -> colorless++;
                case "X" -> xSymbols++;
                default -> {
                    if (symbol.matches("\\d+")) generic = Math.addExact(generic, Integer.parseInt(symbol));
                    else throw new IllegalArgumentException("Unsupported mana symbol {" + symbol + "}");
                }
            }
            end = matcher.end();
        }
        if (end != input.length()) throw new IllegalArgumentException("Malformed mana cost: " + value);
        return new ManaCost(generic, white, blue, black, red, green, colorless, xSymbols);
    }

    public int manaValue() {
        return generic + white + blue + black + red + green + colorless;
    }

    public int manaValueOnStack(int chosenX) {
        if (chosenX < 0) throw new IllegalArgumentException("Chosen X cannot be negative");
        return Math.addExact(manaValue(), Math.multiplyExact(xSymbols, chosenX));
    }
}
