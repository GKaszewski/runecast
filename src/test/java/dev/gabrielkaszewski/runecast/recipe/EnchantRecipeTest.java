package dev.gabrielkaszewski.runecast.recipe;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EnchantRecipeTest {
    @Test
    void ingredientHoldsItemIdAndCount() {
        EnchantIngredient ing = new EnchantIngredient("minecraft:flint", 2);
        assertEquals("minecraft:flint", ing.getItemId());
        assertEquals(2, ing.getCount());
    }

    @Test
    void recipeHoldsAllFields() {
        List<String> targets = List.of("minecraft:wooden_sword");
        List<EnchantIngredient> ings = List.of(new EnchantIngredient("minecraft:flint", 2));
        EnchantRecipe recipe = new EnchantRecipe("runecast:sharpness", 0, 1, targets, ings);
        assertEquals("runecast:sharpness", recipe.getEnchantmentId());
        assertEquals(0, recipe.getFromLevel());
        assertEquals(1, recipe.getToLevel());
        assertEquals(targets, recipe.getTargets());
        assertEquals(ings, recipe.getIngredients());
    }
}
