package dev.gabrielkaszewski.runecast.effect;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface CustomMiningEffect {
    String UNDERWATER_MINING_BOOST_ID = "runecast:underwater_mining_boost";
    CustomMiningEffect UNDERWATER_MINING_BOOST = (level, speed, block, miner) -> {
        if (!(miner instanceof PlayerEntity player)) return speed;
        if (!player.isSubmergedInWater()) return speed;
        return speed * 5.0f;
    };

    /** Full context available; @param level enchantment level; return modified speed */
    float apply(int level, float currentSpeed, Block block, LivingEntity miner);
}
