package dev.gabrielkaszewski.runecast.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

public final class ItemIds {
    private ItemIds() {}

    public static String resolve(ItemStack stack) {
        if (stack == null || stack.itemId < 0 || stack.itemId >= Item.ITEMS.length) return null;
        Item item = Item.ITEMS[stack.itemId];
        if (item == null) return null;
        Identifier id = ItemRegistry.INSTANCE.getId(item);
        if (id == null) {
            // Unregistered item — possible from another mod; ritual cannot reference it by name.
            return null;
        }
        return id.toString();
    }
}
