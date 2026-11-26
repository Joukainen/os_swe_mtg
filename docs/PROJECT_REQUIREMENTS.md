# os_swe_mtg — project requirements baseline

## Product goal

Build a server-authoritative online 1v1 Magic client for two human players. AI is not a product feature or delivery dependency. The permitted sets are Alpha, Beta, Unlimited, Arabian Nights, Antiquities, Revised, Legends and The Dark only.

## Product priorities

1. Reliable online 1v1 with authenticated players and synchronized authoritative game state.
2. Complete local catalogue for the eight permitted sets, including all qualifying printings.
3. General-purpose Magic rules foundation: turn structure, special actions, casting, costs, priority, stack, triggers, APNAP, resolution, state-based actions, continuous effects, replacement effects and combat.
4. Deck builder, validation, best-of-three lifecycle and sideboarding.
5. Responsive browser UI for desktop, tablet and mobile.

## Card data versus card rules

A card present in the catalogue is not automatically rules-supported. A card can be marked as supported only when its behavior has implementation and relevant regression tests. Oracle-level identity, individual printings and in-game instances are separate.

The intended card data pipeline is a local, versioned bulk import. No live Scryfall request should be required during a match. Before distributing or monetizing Scryfall-derived data/images, review the current terms and obtain permission if needed. This branch includes no Scryfall dataset or images.

## Rules requirements

- Only playing a land is a special action from hand to battlefield. It does not use the stack.
- Every other spell is cast onto the stack and only becomes a permanent after resolution.
- The normal draw for turn occurs before the active player receives priority in the draw step. Draw triggers are collected after the draw and put on the stack before priority.
- Upkeep and other triggered abilities do not resolve immediately. They are put onto the stack at the correct checkpoint, then players receive priority.
- Simultaneous triggers use APNAP ordering. Each player chooses the order of their own simultaneous triggers. All such triggers are on the stack before priority.
- Trigger values are read at the rules-defined time. Black Vise damage is calculated on resolution.
- The Abyss is an upkeep-triggered ability, not a continuous effect. Its World supertype is handled separately by the World rule as an SBA.
- State-based actions do not use the stack and are checked repeatedly at relevant checkpoints until the state is stable.
- UI step stops are optional client preferences. They may pause automatic passing but cannot create priority or change game rules. Holding priority is a separate preference.

## Match requirements

A best-of-three match registers main deck and sideboard for both players. Card data and required images should be available locally before Game 1 and remain available through sideboarding and Games 2 and 3, without live card-data API calls during games.

## Quality bar

A milestone is complete only when code, tests and limitations are documented. Do not claim tournament accuracy, complete card rules or playable online matches while core rule systems, match lifecycle and security remain incomplete.
