package dev.gabrielkaszewski.runecast.effect;

import java.util.List;

/**
 * Complete declaration of one enchantment: its identity, display name,
 * max level, and the list of effects it applies at use-time.
 */
public record EffectDefinition(
    String enchantmentId,
    String displayName,
    int maxLevel,
    List<Effect> effects
) {}
