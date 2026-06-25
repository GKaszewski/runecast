package dev.gabrielkaszewski.runecast.recipe;

import dev.gabrielkaszewski.runecast.detection.RitualDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class RitualDetectorTest {
    // Manually populate RecipeRegistry via parseFile before each test.
    // RecipeRegistry.load() uses FabricLoader so we can't call it in unit tests.

    private static final String SAMPLE_JSON = """
        {
          "enchantment": "runecast:sharpness",
          "recipes": [
            {
              "from_level": 0,
              "to_level": 1,
              "targets": ["minecraft:wooden_sword"],
              "ingredients": [
                {"item": "minecraft:flint", "count": 2}
              ]
            }
          ]
        }
        """;

    @BeforeEach
    void setup() {
        // Directly inject parsed recipes into the registry via a test helper.
        // We add a package-private method to RecipeRegistry for this.
        RecipeRegistry.loadFromList(RecipeRegistry.parseFile(SAMPLE_JSON));
    }

    @Test
    void matchesWhenAllConditionsMet() {
        EnchantRecipe match = RitualDetector.findMatch(
            "minecraft:wooden_sword",
            Map.of(),
            Map.of("minecraft:flint", 2)
        );
        assertNotNull(match);
        assertEquals("runecast:sharpness", match.getEnchantmentId());
    }

    @Test
    void noMatchWhenTargetWrongType() {
        assertNull(RitualDetector.findMatch(
            "minecraft:stone_pickaxe",
            Map.of(),
            Map.of("minecraft:flint", 2)
        ));
    }

    @Test
    void noMatchWhenIngredientsInsufficient() {
        assertNull(RitualDetector.findMatch(
            "minecraft:wooden_sword",
            Map.of(),
            Map.of("minecraft:flint", 1)
        ));
    }

    @Test
    void noMatchWhenFromLevelMismatch() {
        // recipe requires from_level=0 but item already has sharpness 1
        assertNull(RitualDetector.findMatch(
            "minecraft:wooden_sword",
            Map.of("runecast:sharpness", 1),
            Map.of("minecraft:flint", 2)
        ));
    }

    @Test
    void matchesExactIngredientCount() {
        // 3 flints when recipe needs 2 — surplus is fine
        assertNotNull(RitualDetector.findMatch(
            "minecraft:wooden_sword",
            Map.of(),
            Map.of("minecraft:flint", 3)
        ));
    }
}
