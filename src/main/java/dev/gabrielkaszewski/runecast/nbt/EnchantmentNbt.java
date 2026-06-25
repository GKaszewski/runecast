package dev.gabrielkaszewski.runecast.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.item.StationItemStack;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;

import java.util.HashMap;
import java.util.Map;

public final class EnchantmentNbt {
    private static final String KEY = "Enchantments";

    private EnchantmentNbt() {}

    public static Map<String, Integer> getEnchantments(ItemStack stack) {
        Map<String, Integer> result = new HashMap<>();
        if (stack == null) return result;
        NbtCompound nbt = ((StationItemStack) stack).getStationNbt();
        if (!nbt.contains(KEY)) return result;
        NbtList list = nbt.getList(KEY);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = (NbtCompound) list.get(i);
            result.put(entry.getString("id"), entry.getInt("lvl"));
        }
        return result;
    }

    public static void setEnchantment(ItemStack stack, String enchantmentId, int level) {
        NbtCompound nbt = ((StationItemStack) stack).getStationNbt();
        NbtList list = nbt.contains(KEY) ? nbt.getList(KEY) : new NbtList();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = (NbtCompound) list.get(i);
            if (entry.getString("id").equals(enchantmentId)) {
                entry.putInt("lvl", level);
                nbt.put(KEY, list);
                // StationAPI has no public setter for station NBT; impl.* is the only path
                StationNBTSetter.cast(stack).setStationNbt(nbt);
                return;
            }
        }
        NbtCompound entry = new NbtCompound();
        entry.putString("id", enchantmentId);
        entry.putInt("lvl", level);
        list.add(entry);
        nbt.put(KEY, list);
        // StationAPI has no public setter for station NBT; impl.* is the only path
        StationNBTSetter.cast(stack).setStationNbt(nbt);
    }
}
