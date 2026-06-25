package dev.gabrielkaszewski.runecast.detection;

import dev.gabrielkaszewski.runecast.recipe.EnchantRecipe;

import java.util.Map;
import java.util.Set;

public final class RitualScanner {
    private RitualScanner() {}

    /**
     * Pure matching logic — no Minecraft imports.
     *
     * @param targetId         registry ID of the target item (e.g. "minecraft:wooden_sword")
     * @param enchantments     current enchantments on the target (id → level)
     * @param ingredientCounts counts of non-target items in the ritual radius (id → total count)
     * @param otherItemIds     IDs of all non-target items in the ritual radius (for ambiguity check)
     * @return matching recipe, or null if the cluster is ambiguous or no recipe matches
     */
    public static EnchantRecipe scan(
        String targetId,
        Map<String, Integer> enchantments,
        Map<String, Integer> ingredientCounts,
        Set<String> otherItemIds
    ) {
        for (String otherId : otherItemIds) {
            if (RitualDetector.isValidTarget(otherId)) return null;
        }
        return RitualDetector.findMatch(targetId, enchantments, ingredientCounts);
    }
}
