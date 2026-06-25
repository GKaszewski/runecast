package dev.gabrielkaszewski.runecast.detection;

import dev.gabrielkaszewski.runecast.recipe.RecipeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class RitualScannerTest {
    private static final String SAMPLE_JSON = """
        {
          "enchantment": "runecast:sharpness",
          "recipes": [
            {
              "from_level": 0,
              "to_level": 1,
              "targets": ["minecraft:wooden_sword"],
              "ingredients": [{"item": "minecraft:flint", "count": 2}]
            }
          ]
        }
        """;

    @BeforeEach
    void setup() {
        RecipeRegistry.loadFromList(RecipeRegistry.parseFile(SAMPLE_JSON));
    }

    @Test
    void matchesWhenUnambiguousAndIngredientsPresent() {
        assertNotNull(RitualScanner.scan(
            "minecraft:wooden_sword",
            Map.of(),
            Map.of("minecraft:flint", 2),
            Set.of("minecraft:dirt")
        ));
    }

    @Test
    void returnsNullWhenAmbiguousTargetPresent() {
        // another wooden_sword in range → ambiguous
        assertNull(RitualScanner.scan(
            "minecraft:wooden_sword",
            Map.of(),
            Map.of("minecraft:flint", 2),
            Set.of("minecraft:wooden_sword")
        ));
    }

    @Test
    void returnsNullWhenIngredientsInsufficient() {
        assertNull(RitualScanner.scan(
            "minecraft:wooden_sword",
            Map.of(),
            Map.of("minecraft:flint", 1),
            Set.of()
        ));
    }

    @Test
    void returnsNullWhenFromLevelMismatch() {
        assertNull(RitualScanner.scan(
            "minecraft:wooden_sword",
            Map.of("runecast:sharpness", 1),
            Map.of("minecraft:flint", 2),
            Set.of()
        ));
    }
}
