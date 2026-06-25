# Handoff: Runecast Mod

## Context

The user is a Minecraft b1.7.3 modder working in `/mnt/drive/dev/runecast`. Stack: StationAPI 2.0, Fabric Loader, Mixin. Reference mod at `/mnt/drive/dev/fun-with-modding`.

## Runecast Mod

**Name:** Runecast
**Tagline:** "Enchant tools and gear by throwing items on the ground."
**Target:** Minecraft b1.7.3 via StationAPI 2.0

### Concept

An enchanting system with no block or UI. The player throws vanilla items on the ground near a target item (tool/weapon/armor, also on the ground). When a valid recipe match is detected and a settle timer elapses, ingredients are consumed and the target gains an enchantment as NBT.

### Architecture

- **Detection** — `EntityItem.tick()` mixin on the target item; scans a 1-block AABB each tick; starts a 20-tick settle timer when a valid ritual is found; re-validates at fire time only
- **Recipe system** — JSON files at `data/runecast/enchant_recipes/<id>.json`, loaded by a custom directory scanner at startup; one file per enchantment
- **Application** — server-side: writes NBT to target item, removes consumed ingredient entities, sends `EnchantParticlePacket` to client
- **Behavior** — `EnchantmentDispatcher` maps enchantment ids to `BehaviorHandler` instances; thin mixin hooks delegate into the dispatcher
- **Packets** — `EnchantParticlePacket` triggers `enchantmenttable` particles client-side on success; `random.orb` sound also plays

### All Design Decisions Resolved

| Decision | Choice |
|---|---|
| Target location | On the ground as EntityItem |
| Target identification | Whitelist per recipe (`targets` list) |
| Multi-target behaviour | Exactly one valid target — nothing triggers if multiple |
| Ritual radius | 1 block AABB |
| Settle timer | 20 ticks (named constant, not hardcoded) |
| Mid-timer invalidation | Ignored — re-validate at fire time only |
| Timer lives on | Target item (transient field) |
| Level skipping | Allowed — recipe specifies `from_level` and `to_level` |
| Beyond max level | Silently nothing — no matching recipe found |
| Recipes | Data-driven JSON, directory-scanned at startup |
| Recipe file location | `data/runecast/enchant_recipes/<id>.json` |
| Ingredient quantities | Supported via `count` field |
| Success feedback | `enchantmenttable` particles (packet) + `random.orb` sound |
| Client/server | Always packet-based — b1.7.3 has no integrated server |
| Mixin style | Thin callers only — no logic in mixins |
| Decoupling | Domain logic avoids Minecraft imports where possible |

### Enchantments (v1)

| ID | Max Level | Hook |
|---|---|---|
| `runecast:efficiency` | V | Mining speed |
| `runecast:sharpness` | V | Attack damage |
| `runecast:feather_falling` | IV | Fall damage reduction |
| `runecast:aqua_affinity` | I | Underwater mining speed |
| `runecast:unbreaking` | III | Durability chance |

### NBT Format

```
{Enchantments: [{id: "runecast:sharpness", lvl: 2}]}
```

### Recipe File Format

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

### Package Structure

```
dev.gabrielkaszewski.runecast
├── mixin/
├── recipe/
├── detection/
├── enchantment/
│   └── handler/
├── packet/
└── events/init/
```

### Docs

- `CONTEXT.md` — domain glossary
- `docs/adr/0001-single-target-rule.md`
- `docs/adr/0002-data-driven-recipes.md`

### Not Yet Done

- Actual recipe ingredient values (what items unlock each enchantment/level)
- Exact mixin class/method names (verify during implementation)
- Implementation
