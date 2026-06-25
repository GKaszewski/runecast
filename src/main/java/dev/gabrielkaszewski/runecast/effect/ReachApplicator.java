package dev.gabrielkaszewski.runecast.effect;

@FunctionalInterface
public interface ReachApplicator {
    String ATTRIBUTE_ID = "reach_distance";
    ReachApplicator DEFAULT = fv -> fv;

    /** @param formulaValue result of Formula.evaluate(level); return bonus blocks to add to reach */
    float apply(float formulaValue);
}
