# Rules-engine design constraints

## Turn structure and priority stops

A turn uses the actual steps: UNTAP, UPKEEP, DRAW, PRECOMBAT_MAIN, BEGIN_COMBAT, DECLARE_ATTACKERS, DECLARE_BLOCKERS, COMBAT_DAMAGE, END_COMBAT, POSTCOMBAT_MAIN, END_STEP and CLEANUP.

Untap has no priority. Declare attackers, declare blockers and combat damage perform their turn-based action before priority is granted. Cleanup normally has no priority, subject to the Comprehensive Rules exception when state-based actions or triggers are waiting.

The UI supports optional manual stops for each player's own and opponent's steps. A stop only prevents automatic passing; it does not create priority where the rules do not grant it. A stop on the opponent's upkeep is the online equivalent of saying, “I would like to do something in your upkeep.”

## Draw step

The normal draw for turn is a turn-based action, not a spell and not a stack object. The draw happens before the active player receives priority in the draw step. Triggers caused by that draw are collected and put on the stack at the correct checkpoint before priority is given. An opponent cannot remove a draw-trigger source in a priority window between the start of the draw step and the normal draw.

The current foundation records whether the normal draw action has already been performed this turn, but the full trigger checkpoint and turn advancement/reset are future work.

## Land play and casting

Playing a land is a special action: it does not use the stack and puts the land onto the battlefield if legal (active player, priority, own main phase, empty stack, land play available). All other spells are cast: the spell is put on the stack and players receive priority before resolution. A resolved permanent spell becomes a battlefield permanent; an instant or sorcery normally goes to its owner's graveyard after resolution.

## Triggered abilities, APNAP and ordering

When multiple abilities trigger simultaneously, use APNAP: active player orders their triggers first, then nonactive player orders theirs. Each player chooses the relative order of their own simultaneous triggers. All such triggers are put on the stack before any player gets priority. Since the stack resolves last-in-first-out, stack insertion order determines resolution order.

Targets and choices required as a trigger is put onto the stack are made at that time. Other values are evaluated when the rules say they are evaluated; for example, Black Vise damage depends on the opponent's hand size as the trigger resolves.

## Important examples

- The Abyss: an upkeep-triggered ability, not a continuous effect. The active player whose upkeep it is chooses a legal nonartifact creature they control as the target, even when the opponent controls The Abyss. The trigger uses the stack. The World supertype is handled separately by the World rule as an SBA.
- Black Vise: upkeep-triggered ability. It uses the stack; damage is determined on resolution.
- Sheoldred, the Apocalypse (outside the permitted pool): useful generic test fixture. The normal draw happens first; its draw trigger is put onto the stack before priority. Removing Sheoldred after the trigger is on the stack does not remove that trigger.

## State-based actions and priority checkpoints

State-based actions do not use the stack. They are checked at rule-defined checkpoints and repeated until none apply. Waiting triggers are handled with the applicable ordering procedure before priority is granted. Exact sequencing must be checked against the Comprehensive Rules; do not approximate it with ad hoc callbacks.

## Stop system versus priority

A player stop is a UI/automation preference only. Holding priority is separate. Neither setting changes the rules engine's actual priority holder or the turn-step semantics.
