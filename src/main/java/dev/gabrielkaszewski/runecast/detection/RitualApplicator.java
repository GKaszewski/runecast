package dev.gabrielkaszewski.runecast.detection;

import dev.gabrielkaszewski.runecast.nbt.EnchantmentNbt;
import dev.gabrielkaszewski.runecast.packet.EnchantParticlePacket;
import dev.gabrielkaszewski.runecast.recipe.EnchantIngredient;
import dev.gabrielkaszewski.runecast.recipe.EnchantRecipe;
import dev.gabrielkaszewski.runecast.util.ItemIds;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RitualApplicator {
    private RitualApplicator() {}

    public static void apply(World world, ItemEntity target, EnchantRecipe recipe) {
        ItemStack stack = target.stack;
        EnchantmentNbt.setEnchantment(stack, recipe.getEnchantmentId(), recipe.getToLevel());
        target.stack = stack;

        consumeIngredients(world, target, recipe.getIngredients());

        EnchantParticlePacket packet = new EnchantParticlePacket(target.x, target.y, target.z);
        PacketHelper.sendToAllTracking(target, packet);

        world.playSound(target.x, target.y, target.z, "random.orb", 1.0f, 1.0f);
    }

    private static void consumeIngredients(World world, ItemEntity target, List<EnchantIngredient> required) {
        Map<String, Integer> toConsume = new HashMap<>();
        for (EnchantIngredient ing : required) {
            toConsume.put(ing.getItemId(), ing.getCount());
        }

        for (ItemEntity entity : RitualCluster.collect(world, target).entities()) {
            String id = ItemIds.resolve(entity.stack);
            if (id == null) continue;
            int needed = toConsume.getOrDefault(id, 0);
            if (needed <= 0) continue;
            int consumed = Math.min(needed, entity.stack.count);
            entity.stack.count -= consumed;
            if (entity.stack.count <= 0) entity.dead = true;
            toConsume.put(id, needed - consumed);
        }
    }

}
