# Migration notes — JavaScript prototype v52

## Baseline inspected

The supplied v52 archive was inspected before creating this Java branch. Its README describes it as a development build, not a complete rules implementation or tournament-ready online service.

The archive contains:
- 66 hand-authored engine/test card records in `src/cards.js`.
- JavaScript modules for turn flow, zones, mana, triggers, continuous effects and activated abilities.
- A prototype online session manager and HTTP/WebSocket server.
- A Scryfall bulk import script and local catalogue loader.
- A placeholder `data/cards-alpha-to-dark.json` marked not synced.
- Node test suites.

## Baseline test result

On 2026-11-26, `npm test` was run against the supplied v52 archive with Node v22.16.0. The script completed successfully and printed PASS results for its existing engine and catalogue regression groups.

This is evidence that the v52 test script passed in that environment. It is not proof of complete Magic rules coverage, complete card data, production security or reliable online multiplayer.

## Observations for migration

1. The old turn module combines step entry, turn-based actions and callbacks. The new Java engine should keep turn-based actions, trigger checkpoints and priority grants explicit.
2. The old hand-authored list contains 66 records; it is not the full Alpha–The Dark catalogue.
3. The old session manager is useful as a behavior reference, but Java must independently establish authenticated identity, command authorization, state revisions, per-player views and reconnect behavior.
4. The v52 README lists incomplete layers, replacement effects, costs/choices/targets and card-specific interactions.

## Migration policy

Do not copy the old architecture wholesale. Port useful, tested behavior only after re-expressing it in the new domain model and adding Java regression tests. Keep the original v52 archive separate until deliberately archived in Git.
