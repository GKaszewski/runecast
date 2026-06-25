package dev.gabrielkaszewski.runecast.detection;

import dev.gabrielkaszewski.runecast.recipe.EnchantIngredient;
import dev.gabrielkaszewski.runecast.recipe.EnchantRecipe;
import dev.gabrielkaszewski.runecast.recipe.RecipeRegistry;

import java.util.List;
import java.util.Map;

public final class RitualDetector {
    private RitualDetector() {}

    public static boolean isValidTarget(String itemId) {
        if (itemId == null) return false;
        for (EnchantRecipe recipe : RecipeRegistry.getAll()) {
            if (recipe.getTargets().contains(itemId)) return true;
        }
        return false;
    }

    public static EnchantRecipe findMatch(
        String targetItemId,
        Map<String, Integer> currentEnchantments,
        Map<String, Integer> nearbyItemCounts
    ) {
        for (EnchantRecipe recipe : RecipeRegistry.getAll()) {
            if (!recipe.getTargets().contains(targetItemId)) continue;
            int currentLevel = currentEnchantments.getOrDefault(recipe.getEnchantmentId(), 0);
            if (currentLevel != recipe.getFromLevel()) continue;
            if (!hasAllIngredients(recipe.getIngredients(), nearbyItemCounts)) continue;
            return recipe;
        }
        return null;
    }

    private static boolean hasAllIngredients(
        List<EnchantIngredient> required,
        Map<String, Integer> available
    ) {
        for (EnchantIngredient ing : required) {
            if (available.getOrDefault(ing.getItemId(), 0) < ing.getCount()) return false;
        }
        return true;
    }
}
