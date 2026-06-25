package dev.gabrielkaszewski.runecast.effect;

import dev.gabrielkaszewski.runecast.nbt.EnchantmentNbt;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.function.BiFunction;

public final class EffectRegistry {
    private static final Map<String, EffectDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<String, MiningApplicator> MINING = new LinkedHashMap<>();
    private static final Map<String, AttackApplicator> ATTACK = new LinkedHashMap<>();
    private static final Map<String, FallApplicator> FALL = new LinkedHashMap<>();
    private static final Map<String, DurabilityApplicator> DURABILITY = new LinkedHashMap<>();
    private static final Map<String, CustomMiningEffect> CUSTOM_MINING = new LinkedHashMap<>();
    private static final Map<String, KnockbackApplicator> KNOCKBACK = new LinkedHashMap<>();
    private static final Map<String, ReachApplicator> REACH = new LinkedHashMap<>();
    private static final Map<String, LootApplicator> LOOT = new LinkedHashMap<>();
    private static final Random RANDOM = new Random();

    private EffectRegistry() {}

    // ── Registration ────────────────────────────────────────────────────────

    public static void registerDefinition(EffectDefinition def) { DEFINITIONS.put(def.enchantmentId(), def); }
    public static void registerMining(String id, MiningApplicator app)       { MINING.put(id, app); }
    public static void registerAttack(String id, AttackApplicator app)       { ATTACK.put(id, app); }
    public static void registerFall(String id, FallApplicator app)           { FALL.put(id, app); }
    public static void registerDurability(String id, DurabilityApplicator a) { DURABILITY.put(id, a); }
    public static void registerCustomMining(String id, CustomMiningEffect e) { CUSTOM_MINING.put(id, e); }
    public static void registerKnockback(String id, KnockbackApplicator app) { KNOCKBACK.put(id, app); }
    public static void registerReach(String id, ReachApplicator app)         { REACH.put(id, app); }
    public static void registerLoot(String id, LootApplicator app)           { LOOT.put(id, app); }

    public static EffectDefinition getDefinition(String enchantmentId) {
        return DEFINITIONS.get(enchantmentId);
    }

    // ── Dispatch ─────────────────────────────────────────────────────────────

    /** Accumulated bonus: sum of each matching applicator's contribution. */
    public static float computeAttackBonus(ItemStack stack)   { return sumGenericEffects(stack, ATTACK,    AttackApplicator::apply); }
    public static float computeKnockbackBonus(ItemStack stack) { return sumGenericEffects(stack, KNOCKBACK, KnockbackApplicator::apply); }
    public static float computeReachBonus(ItemStack stack)    { return sumGenericEffects(stack, REACH,     ReachApplicator::apply); }
    public static float computeLootBonus(ItemStack stack)     { return sumGenericEffects(stack, LOOT,      LootApplicator::apply); }

    /** Fold: each applicator transforms the running value. */
    public static float applyFall(ItemStack boots, float distance) {
        return foldGenericEffects(boots, distance, FALL, (app, cur, fv) -> app.apply(cur, fv));
    }

    /** Mining: fold with full context + custom effects. */
    public static float applyMining(ItemStack stack, float speed, Block block, LivingEntity miner) {
        return foldEffects(stack, speed, (acc, effect, level) -> {
            if (effect instanceof Effect.Generic g) {
                MiningApplicator app = MINING.get(g.attributeId());
                return app != null ? app.apply(acc, g.formula().evaluate(level), block, miner) : acc;
            }
            if (effect instanceof Effect.Custom c) {
                CustomMiningEffect custom = CUSTOM_MINING.get(c.customId());
                return custom != null ? custom.apply(level, acc, block, miner) : acc;
            }
            return acc;
        });
    }

    /** Veto: returns false if any durability applicator says skip. */
    public static boolean shouldApplyDurability(ItemStack stack) {
        return foldEffects(stack, true, (acc, effect, level) -> {
            if (!acc) return false;
            if (effect instanceof Effect.Generic g) {
                DurabilityApplicator app = DURABILITY.get(g.attributeId());
                return app == null || app.shouldApply(g.formula().evaluate(level), RANDOM);
            }
            return acc;
        });
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Iterates all Generic effects on a stack, summing applicator contributions.
     * @param fn (applicator, formulaValue) → float contribution
     */
    private static <T> float sumGenericEffects(ItemStack stack, Map<String, T> applicators,
                                                BiFunction<T, Float, Float> fn) {
        float total = 0f;
        for (var e : EnchantmentNbt.getEnchantments(stack).entrySet()) {
            EffectDefinition def = DEFINITIONS.get(e.getKey());
            if (def == null) continue;
            int level = e.getValue();
            for (Effect effect : def.effects()) {
                if (effect instanceof Effect.Generic g) {
                    T app = applicators.get(g.attributeId());
                    if (app != null) total += fn.apply(app, g.formula().evaluate(level));
                }
            }
        }
        return total;
    }

    /**
     * Iterates all Generic effects on a stack, folding each applicator into an accumulator.
     * @param fn (applicator, currentValue, formulaValue) → newValue
     */
    private static <T> float foldGenericEffects(ItemStack stack, float init, Map<String, T> applicators,
                                                  FoldFn<T> fn) {
        float acc = init;
        for (var e : EnchantmentNbt.getEnchantments(stack).entrySet()) {
            EffectDefinition def = DEFINITIONS.get(e.getKey());
            if (def == null) continue;
            int level = e.getValue();
            for (Effect effect : def.effects()) {
                if (effect instanceof Effect.Generic g) {
                    T app = applicators.get(g.attributeId());
                    if (app != null) acc = fn.apply(app, acc, g.formula().evaluate(level));
                }
            }
        }
        return acc;
    }

    @FunctionalInterface
    private interface FoldFn<T> {
        float apply(T applicator, float current, float formulaValue);
    }

    @FunctionalInterface
    private interface EffectFn<T> {
        T apply(T acc, Effect effect, int level);
    }

    private static <T> T foldEffects(ItemStack stack, T init, EffectFn<T> fn) {
        T acc = init;
        for (var e : EnchantmentNbt.getEnchantments(stack).entrySet()) {
            EffectDefinition def = DEFINITIONS.get(e.getKey());
            if (def == null) continue;
            int level = e.getValue();
            for (Effect effect : def.effects()) {
                acc = fn.apply(acc, effect, level);
            }
        }
        return acc;
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Clears all registered definitions and loads {@code defs}.
     * Called from InitListener at startup and from tests for isolation.
     */
    public static void loadDefinitions(List<EffectDefinition> defs) {
        DEFINITIONS.clear();
        defs.forEach(d -> DEFINITIONS.put(d.enchantmentId(), d));
    }
}
