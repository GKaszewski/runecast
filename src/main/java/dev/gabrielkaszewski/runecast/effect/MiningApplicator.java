package dev.gabrielkaszewski.runecast.effect;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface MiningApplicator {
    String ATTRIBUTE_ID = "mining_speed";
    MiningApplicator DEFAULT = (speed, fv, block, miner) -> speed * fv;

    /** @param currentSpeed current mining speed; @param formulaValue result of Formula.evaluate(level) */
    float apply(float currentSpeed, float formulaValue, Block block, LivingEntity miner);
}
