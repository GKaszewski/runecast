package dev.gabrielkaszewski.runecast.effect;

@FunctionalInterface
public interface KnockbackApplicator {
    String ATTRIBUTE_ID = "knockback_bonus";
    KnockbackApplicator DEFAULT = fv -> fv;

    /** @param formulaValue result of Formula.evaluate(level); return knockback strength (cast to int by caller) */
    float apply(float formulaValue);
}
