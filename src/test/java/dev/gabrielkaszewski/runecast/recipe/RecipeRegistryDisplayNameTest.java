package dev.gabrielkaszewski.runecast.recipe;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeRegistryDisplayNameTest {
    private String loadTestJson() throws Exception {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("data/runecast/enchant_recipes/test_sharpness.json")) {
            assertNotNull(is, "test JSON not found");
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void parsesDisplayName() throws Exception {
        String json = loadTestJson();
        RecipeRegistry.loadFromList(RecipeRegistry.parseFile(json));
        String displayName = RecipeRegistry.parseDisplayName(json);
        RecipeRegistry.putDisplayName("runecast:sharpness", displayName);
        assertEquals("Sharpness", RecipeRegistry.getDisplayName("runecast:sharpness"));
    }

    @Test
    void missingDisplayNameReturnsNull() throws Exception {
        RecipeRegistry.loadFromList(List.of());
        assertNull(RecipeRegistry.getDisplayName("runecast:unknown"));
    }
}
