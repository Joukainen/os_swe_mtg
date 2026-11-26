# os_swe_mtg

Online 1v1 Swedish Old School Magic client and rules engine, restricted to Alpha–The Dark.

## Status: architecture foundation under review

This repository is being migrated from the earlier JavaScript prototype (v52) to a Java 21 / Spring Boot backend. The v52 prototype remains a separate historical reference and is not treated as a complete rules engine.

The current branch contains a deliberately narrow foundation:

- Java 21 and Spring Boot 4.2.0.
- Separate `CardDefinition`, `CardPrinting`, and `CardInstance` models.
- A mana-cost parser for coloured, colourless, generic and X symbols.
- A two-player immutable `GameState`, explicit zones, per-copy identity, stack model and priority state.
- Validation that each card instance appears in exactly one matching zone.
- A pure `GameEngine` slice for the normal turn draw (once per turn) and special-action land play.
- A WebSocket JSON envelope that uses the authenticated WebSocket principal instead of trusting a player ID in the payload.
- Player-specific view projection and baseline JUnit tests.

**This is not yet a playable online game.** Casting and mana payment, priority passing, stack resolution, APNAP trigger ordering, state-based actions, continuous effects, replacement effects, combat, authenticated lobby/match lifecycle, reconnect, deck builder, sideboard and Scryfall import are not complete. Game-changing WebSocket commands are deliberately rejected rather than simulated.

## Requirements

- JDK 21
- Maven 3.6.3 or later

## Run checks

```bash
mvn --batch-mode verify
```

## Run the application

```bash
mvn spring-boot:run
```

The WebSocket endpoint is `/ws/games`. The initial transport only answers PING and rejects gameplay commands. Spring Security's default development authentication is active; do not expose this build publicly. Production identity, authorization, session lifecycle and deployment security are future requirements.

## Branch strategy

- `main`: stable baseline only.
- `develop`: integration branch.
- `architecture/java-spring-foundation`: current architecture and code-review proposal.

Changes should enter `develop` through pull requests. Do not commit experimental engine work directly to `main`.

## Card data policy

The intended card pool is Alpha, Beta, Unlimited, Arabian Nights, Antiquities, Revised, Legends and The Dark only. Scryfall data import and licensing review are separate milestones; this branch does not bundle Scryfall data or card images.
