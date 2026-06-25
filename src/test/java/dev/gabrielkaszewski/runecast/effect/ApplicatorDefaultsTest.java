package dev.gabrielkaszewski.runecast.effect;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import dev.gabrielkaszewski.runecast.effect.CustomMiningEffect;

public class ApplicatorDefaultsTest {

    @Test
    void miningDefaultScalesSpeedByFormulaValue() {
        // speed * fv: 2.0 * 1.5 = 3.0
        assertEquals(3.0f, MiningApplicator.DEFAULT.apply(2.0f, 1.5f, null, null), 0.001f);
    }

    @Test
    void attackDefaultIsIdentity() {
        assertEquals(2.5f, AttackApplicator.DEFAULT.apply(2.5f), 0.001f);
    }

    @Test
    void fallDefaultScalesDistanceByFormulaValue() {
        // distance * fv: 10.0 * 0.6 = 6.0
        assertEquals(6.0f, FallApplicator.DEFAULT.apply(10.0f, 0.6f), 0.001f);
    }

    @Test
    void durabilityDefaultReturnsTrueWhenRandomBelowThreshold() {
        Random alwaysLow = new Random() { @Override public float nextFloat() { return 0.1f; } };
        assertTrue(DurabilityApplicator.DEFAULT.shouldApply(0.25f, alwaysLow));
    }

    @Test
    void durabilityDefaultReturnsFalseWhenRandomAboveThreshold() {
        Random alwaysHigh = new Random() { @Override public float nextFloat() { return 0.9f; } };
        assertFalse(DurabilityApplicator.DEFAULT.shouldApply(0.25f, alwaysHigh));
    }

    @Test
    void knockbackDefaultIsIdentity() {
        assertEquals(1.5f, KnockbackApplicator.DEFAULT.apply(1.5f), 0.001f);
    }

    @Test
    void reachDefaultIsIdentity() {
        assertEquals(2.0f, ReachApplicator.DEFAULT.apply(2.0f), 0.001f);
    }

    @Test
    void lootDefaultIsIdentity() {
        assertEquals(3.0f, LootApplicator.DEFAULT.apply(3.0f), 0.001f);
    }

    @Test
    void attributeIdsMatchJsonKeys() {
        assertEquals("mining_speed",          MiningApplicator.ATTRIBUTE_ID);
        assertEquals("attack_damage",         AttackApplicator.ATTRIBUTE_ID);
        assertEquals("fall_damage_reduction", FallApplicator.ATTRIBUTE_ID);
        assertEquals("durability_skip_chance",DurabilityApplicator.ATTRIBUTE_ID);
        assertEquals("knockback_bonus",       KnockbackApplicator.ATTRIBUTE_ID);
        assertEquals("reach_distance",        ReachApplicator.ATTRIBUTE_ID);
        assertEquals("loot_bonus",            LootApplicator.ATTRIBUTE_ID);
    }

    @Test
    void underwaterMiningBoostReturnsSpeedUnchangedForNonPlayer() {
        // miner is null — instanceof PlayerEntity check returns false → identity
        assertEquals(2.0f, CustomMiningEffect.UNDERWATER_MINING_BOOST.apply(1, 2.0f, null, null), 0.001f);
    }

    @Test
    void underwaterBoostIdMatchesRegistrationKey() {
        assertEquals("runecast:underwater_mining_boost", CustomMiningEffect.UNDERWATER_MINING_BOOST_ID);
    }
}
