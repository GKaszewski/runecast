package dev.gabrielkaszewski.runecast.effect;

public sealed interface Formula permits Formula.AdditiveMul, Formula.AdditiveFlat, Formula.Reductive, Formula.InverseChance {

    float evaluate(int level);

    /** value *= 1 + perLevel * level */
    record AdditiveMul(float perLevel) implements Formula {
        public float evaluate(int level) { return 1f + perLevel * level; }
    }

    /** value += perLevel * level */
    record AdditiveFlat(float perLevel) implements Formula {
        public float evaluate(int level) { return perLevel * level; }
    }

    /** value *= max(1 - perLevel * level, 1 - cap) — returns remaining fraction */
    record Reductive(float perLevel, float cap) implements Formula {
        public float evaluate(int level) { return Math.max(1f - perLevel * level, 1f - cap); }
    }

    /** Returns pass probability = 1 / (level + denominator) */
    record InverseChance(float denominator) implements Formula {
        public float evaluate(int level) { return 1f / (level + denominator); }
    }
}
