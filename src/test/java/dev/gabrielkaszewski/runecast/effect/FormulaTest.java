package dev.gabrielkaszewski.runecast.effect;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FormulaTest {
    @Test void additiveMulLevel4() {
        assertEquals(2.2f, new Formula.AdditiveMul(0.3f).evaluate(4), 0.001f);
    }
    @Test void additiveMulLevel0() {
        assertEquals(1.0f, new Formula.AdditiveMul(0.3f).evaluate(0), 0.001f);
    }
    @Test void additiveFlatLevel3() {
        assertEquals(1.5f, new Formula.AdditiveFlat(0.5f).evaluate(3), 0.001f);
    }
    @Test void reductiveLevel2BelowCap() {
        // per_level=0.2, cap=0.8, level=2 → remaining = max(1-0.4, 1-0.8) = max(0.6, 0.2) = 0.6
        assertEquals(0.6f, new Formula.Reductive(0.2f, 0.8f).evaluate(2), 0.001f);
    }
    @Test void reductiveLevel5AtCap() {
        // per_level=0.2, cap=0.8, level=5 → 1-1.0=0.0, but cap means min remaining = 1-0.8=0.2
        assertEquals(0.2f, new Formula.Reductive(0.2f, 0.8f).evaluate(5), 0.001f);
    }
    @Test void inverseChanceLevel3() {
        // denominator=1, level=3 → 1/(3+1) = 0.25
        assertEquals(0.25f, new Formula.InverseChance(1f).evaluate(3), 0.001f);
    }
    @Test void inverseChanceLevel1() {
        assertEquals(0.5f, new Formula.InverseChance(1f).evaluate(1), 0.001f);
    }
}
