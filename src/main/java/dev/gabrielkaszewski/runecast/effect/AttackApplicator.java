package dev.gabrielkaszewski.runecast.effect;

@FunctionalInterface
public interface AttackApplicator {
    String ATTRIBUTE_ID = "attack_damage";
    AttackApplicator DEFAULT = fv -> fv;

    /** @param formulaValue result of Formula.evaluate(level); returns bonus damage to add */
    float apply(float formulaValue);
}
