# ADR 0002: Data-Driven JSON Recipes

## Status
Accepted

## Decision
Enchanting recipes are defined in JSON files at `data/runecast/enchant_recipes/<enchantment_id>.json`, loaded by a custom directory scanner at startup. One file per enchantment.

## Reasons
- Adding enchantments requires zero Java changes
- `data/` is the correct semantic location for game logic (vs `assets/` for visuals)
- StationAPI's built-in recipe system only covers crafting/smelting — not applicable

## Alternatives considered
- **Hardcoded Java registry**: simpler initially, but requires code changes per enchantment
- **`assets/` directory**: works technically, wrong semantic convention
- **StationAPI recipe system**: only handles crafting/smelting recipe types
