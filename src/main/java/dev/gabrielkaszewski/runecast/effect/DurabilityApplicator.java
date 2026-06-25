package dev.gabrielkaszewski.runecast.effect;

import java.util.Random;

@FunctionalInterface
public interface DurabilityApplicator {
    String ATTRIBUTE_ID = "durability_skip_chance";
    DurabilityApplicator DEFAULT = (fv, random) -> random.nextFloat() < fv;

    /** @param formulaValue probability threshold from InverseChance formula; return false to skip damage */
    boolean shouldApply(float formulaValue, Random random);
}
