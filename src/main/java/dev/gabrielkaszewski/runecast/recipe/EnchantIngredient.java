package dev.gabrielkaszewski.runecast.recipe;

import lombok.Data;

@Data
public class EnchantIngredient {
    private final String itemId;
    private final int count;
}
