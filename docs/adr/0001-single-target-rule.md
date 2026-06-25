# ADR 0001: Exactly One Target Per Ritual

## Status
Accepted

## Decision
If more than one valid target item is within the ritual radius when the settle timer fires, nothing triggers.

## Reasons
- Clean cost model: one ingredient set always = one enchanted item
- No invisible priority logic (e.g. "closest wins") that would surprise the player
- Forces deliberate placement

## Alternatives considered
- **All valid targets**: one ingredient set enchants all nearby targets — breaks cost balance
- **Closest wins**: adds invisible priority logic, hard to communicate to the player
