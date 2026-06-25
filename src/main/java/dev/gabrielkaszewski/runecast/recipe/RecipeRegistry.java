package dev.gabrielkaszewski.runecast.recipe;

import com.google.gson.*;
import dev.gabrielkaszewski.runecast.effect.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public final class RecipeRegistry {
    private static final List<EnchantRecipe> RECIPES = new ArrayList<>();
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger("runecast");
    private static final Map<String, String> DISPLAY_NAMES = new LinkedHashMap<>();

    private RecipeRegistry() {}

    public static List<EffectDefinition> load() {
        RECIPES.clear();
        DISPLAY_NAMES.clear();
        List<EffectDefinition> defs = new ArrayList<>();
        ModContainer mod = FabricLoader.getInstance().getModContainer("runecast").orElseThrow();
        Path dir = mod.getRootPaths().get(0).resolve("data/runecast/enchant_recipes");
        if (!Files.isDirectory(dir)) {
            LOGGER.warn("[Runecast] Recipe directory not found: {} — no enchantments will be available", dir);
            return defs;
        }
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    String json = Files.readString(path);
                    EffectDefinition def = loadJsonFile(json);
                    if (def != null) defs.add(def);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load recipe file: " + path, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to list recipe directory", e);
        }
        return defs;
    }

    private static EffectDefinition loadJsonFile(String json) {
        RECIPES.addAll(parseFile(json));
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        String enchantmentId = root.get("enchantment").getAsString();
        if (root.has("display_name")) {
            DISPLAY_NAMES.put(enchantmentId, root.get("display_name").getAsString());
        }
        if (root.has("effects")) {
            return parseEffectDefinition(root);
        }
        return null;
    }

    private static EffectDefinition parseEffectDefinition(JsonObject root) {
        String enchantmentId = root.get("enchantment").getAsString();
        String displayName = root.has("display_name") ? root.get("display_name").getAsString() : enchantmentId;
        int maxLevel = root.has("max_level") ? root.get("max_level").getAsInt() : 1;
        List<Effect> effects = new ArrayList<>();
        for (JsonElement elem : root.getAsJsonArray("effects")) {
            JsonObject obj = elem.getAsJsonObject();
            if (obj.has("custom")) {
                effects.add(new Effect.Custom(obj.get("custom").getAsString()));
            } else {
                effects.add(new Effect.Generic(
                    obj.get("attribute").getAsString(),
                    parseFormula(obj)
                ));
            }
        }
        return new EffectDefinition(enchantmentId, displayName, maxLevel, effects);
    }

    private static Formula parseFormula(JsonObject obj) {
        String shape = obj.get("shape").getAsString();
        return switch (shape) {
            case "additive_multiplier" -> new Formula.AdditiveMul(obj.get("per_level").getAsFloat());
            case "additive_flat"       -> new Formula.AdditiveFlat(obj.get("per_level").getAsFloat());
            case "reductive"           -> new Formula.Reductive(
                                             obj.get("per_level").getAsFloat(),
                                             obj.get("cap").getAsFloat());
            case "inverse_chance"      -> new Formula.InverseChance(obj.get("denominator").getAsFloat());
            default -> throw new IllegalArgumentException("Unknown formula shape: " + shape);
        };
    }

    public static List<EnchantRecipe> parseFile(String json) {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        String enchantmentId = root.get("enchantment").getAsString();

        JsonArray recipesArr = root.getAsJsonArray("recipes");
        List<EnchantRecipe> result = new ArrayList<>();
        for (JsonElement elem : recipesArr) {
            JsonObject obj = elem.getAsJsonObject();
            int fromLevel = obj.get("from_level").getAsInt();
            int toLevel = obj.get("to_level").getAsInt();
            List<String> targets = new ArrayList<>();
            for (JsonElement t : obj.getAsJsonArray("targets")) {
                targets.add(t.getAsString());
            }
            List<EnchantIngredient> ingredients = new ArrayList<>();
            for (JsonElement i : obj.getAsJsonArray("ingredients")) {
                JsonObject ing = i.getAsJsonObject();
                ingredients.add(new EnchantIngredient(
                    ing.get("item").getAsString(),
                    ing.get("count").getAsInt()
                ));
            }
            result.add(new EnchantRecipe(enchantmentId, fromLevel, toLevel, targets, ingredients));
        }
        return result;
    }

    static String parseDisplayName(String json) {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        return root.has("display_name") ? root.get("display_name").getAsString() : null;
    }

    static EffectDefinition loadJsonFileForTest(String json) {
        RECIPES.clear();
        DISPLAY_NAMES.clear();
        return loadJsonFile(json);
    }

    public static List<EnchantRecipe> getAll() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static String getDisplayName(String enchantmentId) {
        return DISPLAY_NAMES.get(enchantmentId);
    }

    public static void loadFromList(List<EnchantRecipe> recipes) {
        RECIPES.clear();
        DISPLAY_NAMES.clear();
        RECIPES.addAll(recipes);
    }

    static void putDisplayName(String enchantmentId, String displayName) {
        DISPLAY_NAMES.put(enchantmentId, displayName);
    }
}
