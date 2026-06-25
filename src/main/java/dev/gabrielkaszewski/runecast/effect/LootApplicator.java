package dev.gabrielkaszewski.runecast.effect;

@FunctionalInterface
public interface LootApplicator {
    String ATTRIBUTE_ID = "loot_bonus";
    LootApplicator DEFAULT = fv -> fv;

    /**
     * @param formulaValue result of Formula.evaluate(level)
     * @return number of extra drop-stack rolls to perform (rounded to int by caller)
     */
    float apply(float formulaValue);
}
