package dev.gabrielkaszewski.runecast.effect;

public sealed interface Effect permits Effect.Generic, Effect.Custom {

    /** Fully JSON-defined: an attribute + formula. No Java code required to add. */
    record Generic(String attributeId, Formula formula) implements Effect {}

    /** References a named Java implementation registered with EffectRegistry. */
    record Custom(String customId) implements Effect {}
}
