package dev.gabrielkaszewski.runecast.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class EffectRegistryTest {
    @BeforeEach
    void reset() {
        EffectRegistry.loadDefinitions(List.of());
    }

    @Test
    void miningSpeedAdditiveMul() {
        EffectDefinition def = new EffectDefinition(
            "runecast:efficiency", "Efficiency", 5,
            List.of(new Effect.Generic("mining_speed", new Formula.AdditiveMul(0.3f)))
        );
        EffectRegistry.loadDefinitions(List.of(def));

        assertEquals("Efficiency", EffectRegistry.getDefinition("runecast:efficiency").displayName());
    }

    @Test
    void unknownEnchantmentReturnsNull() {
        assertNull(EffectRegistry.getDefinition("runecast:nonexistent"));
    }

    @Test
    void formulaEvaluatesCorrectlyThroughRegistry() {
        EffectDefinition def = new EffectDefinition(
            "runecast:sharpness", "Sharpness", 5,
            List.of(new Effect.Generic("attack_damage", new Formula.AdditiveFlat(0.5f)))
        );
        EffectRegistry.loadDefinitions(List.of(def));
        // Formula: AdditiveFlat(0.5) at level 3 → 1.5
        Formula formula = ((Effect.Generic) def.effects().get(0)).formula();
        assertEquals(1.5f, formula.evaluate(3), 0.001f);
    }

    @Test
    void inverseChanceFormulaUsedForDurability() {
        Formula formula = new Formula.InverseChance(1f);
        // level 3 → 1/(3+1) = 0.25
        float fv = formula.evaluate(3);
        // With a fixed "random" of 0.1 < 0.25 → shouldApply=true
        DurabilityApplicator app = (formulaValue, random) -> random.nextFloat() < formulaValue;
        assertTrue(app.shouldApply(fv, new Random() {
            @Override public float nextFloat() { return 0.1f; }
        }));
        // With "random" of 0.9 > 0.25 → shouldApply=false
        assertFalse(app.shouldApply(fv, new Random() {
            @Override public float nextFloat() { return 0.9f; }
        }));
    }
}
