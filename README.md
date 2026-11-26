# os_swe_mtg

Online 1v1 Swedish Old School Magic client and rules engine, restricted to Alpha–The Dark.

## Status: architecture foundation under review

This repository is being migrated from the earlier JavaScript prototype (v52) to a Java 21 / Spring Boot backend. The v52 prototype is retained as historical reference outside this repository; it is not treated as a production rules engine.

The current branch contains a deliberately narrow foundation:

- Java 21 and Spring Boot 3.5.16.
- Separate `CardDefinition`, `CardPrinting`, and `CardInstance` models.
- A mana-cost parser for the symbols needed by the Alpha–The Dark card pool (`W/U/B/R/G/C`, generic numbers and `X`).
- A game state with exactly two players, explicit zones, per-instance identity, a global stack model and priority state.
- Zone-index consistency validation.
- A small pure `GameEngine` surface for normal draw-step draws and land plays.
- A WebSocket JSON envelope that derives the player identity from the authenticated WebSocket principal, never from a client-supplied player ID.
- Unit tests and GitHub Actions CI.

**This is not yet a playable online game.** Spell casting/payment, complete priority passing and resolution, trigger collection/APNAP ordering, state-based actions, continuous effects, combat, authenticated lobby/match lifecycle, reconnect, deck builder and Scryfall import are not complete in this branch. No card other than a land is currently moved directly from hand to battlefield by the engine.

## Requirements

- JDK 21
- Maven 3.6.3 or later (Maven Wrapper will be added in a follow-up once generated and tested)

## Run checks

```bash
mvn -B verify
```

## Run the application

```bash
mvn spring-boot:run
```

The development WebSocket endpoint is `/ws/games`. Spring Security's default development authentication is active; do not expose this build to the public internet. Production identity, origin policy and session authorization require a dedicated security design before deployment.

## Branch strategy

- `main`: stable baseline only.
- `develop`: integration branch.
- `architecture/java-spring-foundation`: current architecture and code-review proposal.

Changes should enter `develop` through pull requests. Do not commit experimental engine work directly to `main`.

## Card data policy

The intended card pool is limited to Alpha, Beta, Unlimited, Arabian Nights, Antiquities, Revised, Legends and The Dark. Scryfall data import and licensing review are separate milestones; this branch does not bundle Scryfall data or card images.
