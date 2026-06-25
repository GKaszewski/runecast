# Runecast — Domain Glossary

## Core Terms

**Enchantment**
A named modifier applied to an item, stored as NBT on the `ItemStack`. Has an id, a current level, and a max level. Each enchantment has an `EffectDefinition` registered with the `EffectRegistry`.

**Target**
The single item on the ground (as `EntityItem`) that is being enchanted. Must match the recipe's `targets` list. Exactly one valid target may be present in the ritual radius — if multiple are found, nothing triggers. The target is also the item whose tick drives detection.

**Ingredient**
A vanilla item dropped on the ground (as `EntityItem`) within 1 block of the target. Consumed server-side when a recipe matches. Ingredients are never tools, weapons, or armor — those categories are reserved for targets.

**Recipe**
A JSON-defined rule that maps a `(target type, from_level, ingredient set)` → `to_level`. Loaded at startup from `data/runecast/enchant_recipes/`. One file per enchantment. Multiple recipe entries per file allow both incremental leveling and level-skipping.

**Ritual**
The act of placing a target and ingredients within range. A ritual is valid when exactly one target and all required ingredients (with correct counts) are within the ritual radius. Triggers after the settle timer elapses, re-validated at fire time.

**Ritual Radius**
1 block. All `EntityItem`s within a 1-block AABB centered on the target are considered part of the ritual.

**Settle Timer**
A countdown (default: 20 ticks / 1 second, defined as a named constant) that begins when a valid ritual is detected on the target's tick. The cluster is re-validated only at fire time — mid-timer invalidation does not cancel the timer.

## Effect System

**Effect**
A modifier applied by an enchantment at use-time. Two kinds: `GenericEffect` (attribute + formula, fully JSON-defined) and `CustomEffect` (references a named Java implementation by ID).

**GenericEffect**
An effect defined entirely in JSON: an attribute ID and a formula shape with parameters. Requires no Java code to add. Example: `{"attribute": "mining_speed", "shape": "additive_multiplier", "per_level": 0.3}`.

**CustomEffect**
An effect referencing a named Java implementation registered with the `EffectRegistry`. Referenced from JSON by ID: `{"custom": "runecast:underwater_mining_boost"}`. Used for conditional or side-effecting behaviors that cannot be expressed as a formula.

**Attribute**
A game property that a `GenericEffect` modifies. Each attribute has a registered `AttributeApplicator` in Java that applies a computed formula value at the correct hook point. Defined vocabulary: `mining_speed`, `attack_damage`, `fall_damage_reduction`, `durability_skip_chance`, `reach_distance`, `knockback_bonus`, `movement_speed`, `arrow_damage`, `loot_bonus`.

**Formula**
A parameterized scaling function applied to the enchantment level to produce a modifier value. Four shapes:
- `additive_multiplier` — `value *= 1 + per_level * level`
- `additive_flat` — `value += per_level * level`
- `reductive` — `value *= 1 - min(per_level * level, cap)`
- `inverse_chance` — `pass_chance = 1 / (level + denominator)` (used for probability effects like Unbreaking)

**AttributeApplicator**
A Java class registered against an attribute ID. Receives a precomputed formula value (a float) and the current game context (tool, target, CIR, etc.), and applies it via the appropriate mixin hook. Replaces the per-enchantment handler classes.

**EffectRegistry**
Central registry mapping attribute IDs → `AttributeApplicator` implementations, and custom effect IDs → `CustomEffect` implementations. Loaded at startup alongside recipes.

**EffectDefinition**
A composition of effects with metadata: enchantment ID, display name, max level, and list of `Effect`s. Declared once; replaces the scattered per-enchantment registrations in InitListener.

## Architecture Constraints

**Thin mixins:** Mixin classes are thin callers only — no logic lives in a mixin. Pattern: Mixin → caller → domain class.

**Client/server split:** Beta 1.7.3 has no integrated server. Any server-side event requiring a client-side effect (particles, sounds) must use an explicit packet.

**Decoupled from Minecraft:** Domain logic avoids direct Minecraft imports where possible. Minecraft types are wrapped or passed as parameters at the boundary.

## Enchantments (v1)

| ID | Max Level | Hook type |
|---|---|---|
| `runecast:efficiency` | V | Tool use (mining speed) |
| `runecast:sharpness` | V | Attack (bonus damage) |
| `runecast:feather_falling` | IV | Damage (fall damage reduction) |
| `runecast:aqua_affinity` | I | Tool use (underwater mining) |
| `runecast:unbreaking` | III | Durability (chance to not consume) |

## NBT Format

Enchantments stored on `ItemStack` as:
```
{Enchantments: [{id: "runecast:sharpness", lvl: 2}]}
```

## Recipe File Format

One JSON file per enchantment at `data/runecast/enchant_recipes/<id>.json`:
```json
{
  "enchantment": "runecast:sharpness",
  "recipes": [
    {
      "from_level": 0,
      "to_level": 1,
      "targets": ["minecraft:wooden_sword", "minecraft:stone_sword"],
      "ingredients": [
        {"item": "minecraft:flint", "count": 2}
      ]
    }
  ]
}
```
