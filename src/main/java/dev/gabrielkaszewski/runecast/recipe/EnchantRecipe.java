package dev.gabrielkaszewski.runecast.recipe;

import lombok.Data;
import java.util.List;

@Data
public class EnchantRecipe {
    private final String enchantmentId;
    private final int fromLevel;
    private final int toLevel;
    private final List<String> targets;
    private final List<EnchantIngredient> ingredients;
}
