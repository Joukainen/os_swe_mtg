package org.osswe.mtg.engine;

public class GameRuleViolation extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public GameRuleViolation(String message) { super(message); }
}
