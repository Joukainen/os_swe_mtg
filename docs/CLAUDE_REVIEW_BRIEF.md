# Claude review brief — architecture/java-spring-foundation

Perform an independent, critical review of this branch. Do not assume the README's claims are correct; inspect the actual source and tests.

## Context

This is the first Java 21 / Spring Boot 4.2.0 architecture slice for `os_swe_mtg`, an online 1v1 human-vs-human Swedish Old School Magic client. The permitted card pool is Alpha, Beta, Unlimited, Arabian Nights, Antiquities, Revised, Legends and The Dark. AI is not a product requirement.

The code is deliberately incomplete. It is a review candidate, not a playable client. Please identify design flaws before more engine code is added.

## Review questions

1. Are `CardDefinition`, `CardPrinting` and `CardInstance` correctly separated? Is Oracle identity versus printing identity safe?
2. Is `CardInstance.zone` plus per-player zone lists a sound source-of-truth design? Check owner versus controller, especially control changes and zone changes.
3. Is `GameState` immutable and validated in a way that will scale to frequent rules transitions?
4. Is `ManaCost.parse` correct for the complete Alpha–The Dark mana symbol vocabulary? Identify unhandled legacy symbols and malformed inputs.
5. Is the special-action land-play timing correct? Ensure no nonland card can be moved directly from hand to battlefield.
6. Does the draw-step API correctly enforce one normal draw per turn and preserve normal-draw -> trigger checkpoint -> priority ordering?
7. Is the WebSocket principal identity boundary sound? What authentication, authorization, origin, CSRF, per-player projection, replay, concurrency and reconnect work remains?
8. Are spell copies representable on the stack (for example, Fork), or does the model incorrectly require every spell object to reference a physical card instance?
9. Are negative life totals and other transient game states representable until state-based actions are checked?
10. Does the Maven/CI setup compile and test correctly on Java 21 with Spring Boot 4.2.0 and Jackson 3?
11. What must be changed before implementing priority passing, spell casting/costs, trigger collection/APNAP, stack resolution, state-based actions and continuous effects?

## Required output

- Findings ranked Critical / High / Medium / Low.
- Exact file and line references for every finding.
- Specific proposed corrections.
- Recommended next three implementation slices.
- Clear separation between real defects and future work that is intentionally not implemented yet.

Do not rewrite the repository or add features. Review only. Do not push or merge changes from the review without explicit approval.
