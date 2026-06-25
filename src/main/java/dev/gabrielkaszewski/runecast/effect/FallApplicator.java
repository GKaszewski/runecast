package dev.gabrielkaszewski.runecast.effect;

@FunctionalInterface
public interface FallApplicator {
    String ATTRIBUTE_ID = "fall_damage_reduction";
    FallApplicator DEFAULT = (distance, fv) -> distance * fv;

    /** @param currentDistance current fall distance; @param formulaValue remaining fraction from Reductive formula */
    float apply(float currentDistance, float formulaValue);
}
