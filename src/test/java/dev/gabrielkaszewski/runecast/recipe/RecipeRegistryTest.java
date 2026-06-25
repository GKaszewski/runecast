package dev.gabrielkaszewski.runecast.recipe;

import dev.gabrielkaszewski.runecast.effect.*;
import dev.gabrielkaszewski.runecast.effect.EffectDefinition;
import dev.gabrielkaszewski.runecast.effect.EffectRegistry;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeRegistryTest {
    private String loadTestJson() throws Exception {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("data/runecast/enchant_recipes/test_sharpness.json")) {
            assertNotNull(is, "test JSON not found on classpath");
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void parsesRecipeCount() throws Exception {
        List<EnchantRecipe> recipes = RecipeRegistry.parseFile(loadTestJson());
        assertEquals(2, recipes.size());
    }

    @Test
    void parsesFirstRecipeFields() throws Exception {
        List<EnchantRecipe> recipes = RecipeRegistry.parseFile(loadTestJson());
        EnchantRecipe r = recipes.get(0);
        assertEquals("runecast:sharpness", r.getEnchantmentId());
        assertEquals(0, r.getFromLevel());
        assertEquals(1, r.getToLevel());
        assertEquals(List.of("minecraft:wooden_sword", "minecraft:stone_sword"), r.getTargets());
        assertEquals(1, r.getIngredients().size());
        assertEquals("minecraft:flint", r.getIngredients().get(0).getItemId());
        assertEquals(2, r.getIngredients().get(0).getCount());
    }

    @Test
    void parsesMultipleIngredients() throws Exception {
        List<EnchantRecipe> recipes = RecipeRegistry.parseFile(loadTestJson());
        EnchantRecipe r = recipes.get(1);
        assertEquals(2, r.getIngredients().size());
    }

    @Test
    void parsesEffectDefinition() throws Exception {
        String json = loadTestJson();
        EffectDefinition def = RecipeRegistry.loadJsonFileForTest(json);
        EffectRegistry.loadDefinitions(def != null ? List.of(def) : List.of());

        var lookup = EffectRegistry.getDefinition("runecast:sharpness");
        assertNotNull(lookup, "EffectDefinition should be registered after explicit loadDefinitions");
        assertEquals("Sharpness", lookup.displayName());
        assertEquals(5, lookup.maxLevel());
        assertEquals(1, lookup.effects().size());

        var effect = lookup.effects().get(0);
        assertInstanceOf(Effect.Generic.class, effect);
        var generic = (Effect.Generic) effect;
        assertEquals("attack_damage", generic.attributeId());
        assertEquals(1.5f, generic.formula().evaluate(3), 0.001f);
    }
}
