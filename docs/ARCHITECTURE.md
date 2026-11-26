# Architecture proposal — `os_swe_mtg`

## Product boundaries

- Primary product: online 1v1 between two human players.
- No AI opponent is a product requirement.
- The permitted card pool is Alpha, Beta, Unlimited, Arabian Nights, Antiquities, Revised, Legends and The Dark only.
- The backend is authoritative. A client submits intentions; it never declares that a rules action succeeded.
- The card database, card rules implementation, and printing/image catalogue are separate concerns.

## Runtime shape

```text
Browser A  -- authenticated WebSocket/JSON --+
                                             |
Browser B  -- authenticated WebSocket/JSON --+--> Spring Boot application
                                                  |-- WebSocket gateway
                                                  |-- Match/session application service
                                                  |-- Rules engine (pure Java domain)
                                                  |-- Card catalogue repository
                                                  |-- Persistence/event log (future)
                                                  +-- PostgreSQL (future)
```

The rules engine must remain independent of Spring, WebSocket, HTTP and persistence. This allows deterministic unit tests without booting a server and prevents transport code from becoming rules code.

## Package boundaries

- `card`: static card definitions, printings, mana parsing and card-data import contracts.
- `game`: aggregate state, players, card instances, zones, stack objects, priority and game steps.
- `engine`: command validation and pure state transitions.
- `rules`: turn-based actions, priority checkpoints, trigger collection/APNAP, stack resolution, SBAs, continuous effects and combat (to be added in separate modules).
- `match`: best-of-three lifecycle, deck registration and sideboarding (future).
- `network`: authenticated WebSocket transport and player-specific views.
- `catalog`: local, version-pinned Alpha–The Dark catalogue and preload manifest (future).

## Core identity model

`CardDefinition` is the Oracle-level logical card. `CardPrinting` is one permitted physical printing and carries Scryfall printing identity and image metadata. `CardInstance` is one copy in one game and has a unique UUID, owner, controller, zone and mutable-in-game characteristics represented immutably through replacement values.

`CardInstance.zone` is the source of truth. `PlayerState` zone lists are ordered indexes into the card-instance map; `GameState` validates that each card occurs in exactly one matching location. Cards on the battlefield are indexed under their current controller; library, hand, graveyard and exile are indexed under their owner. Cards on the stack are represented by `StackObject` entries and their card instances have zone `STACK`.

## Commands and views

- Commands contain a `commandId`, `gameId`, `expectedRevision`, type and typed payload.
- Authenticated player identity is taken from the WebSocket `Principal`, not from client JSON.
- The server validates revision, ownership, timing, priority, costs, targets and game rules before applying a state transition.
- Each player receives a different `PlayerView`; hidden library order and opposing hands must never be serialized to the other player.
- All accepted commands append authoritative events and increment a monotonically increasing revision (event log is a later milestone).

## Deliberate first-slice scope

The current code only implements foundational models, a tested mana parser, a normal draw-step card movement with a `CARD_DRAWN` event, and special-action land play. It intentionally does not claim to implement casting, paying mana, stack resolution, triggers, APNAP, SBAs or a complete turn loop yet.

## Suggested next milestones

1. Priority pass state machine and action checkpoints.
2. Spell casting pipeline: legality -> choices/targets -> cost payment -> spell object on stack; resolution moves a permanent to battlefield or an instant/sorcery to graveyard.
3. Trigger system: collect simultaneous triggers, APNAP ordering, owner-selected ordering, then priority. Trigger values that depend on game state are evaluated at the rule-defined time (for example, Black Vise damage on resolution).
4. State-based action loop and World/Legend rules.
5. Continuous effects/layers, replacement effects and combat.
6. Authentication-backed room/match lifecycle, PlayerView projections and reconnect.
7. Scryfall import, deck validation, sideboard and best-of-three preload.
