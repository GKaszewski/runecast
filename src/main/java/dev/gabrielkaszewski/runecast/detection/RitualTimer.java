package dev.gabrielkaszewski.runecast.detection;

public final class RitualTimer {
    private final int settleTicks;
    private int remaining = -1; // -1 = idle

    public RitualTimer(int settleTicks) {
        this.settleTicks = settleTicks;
    }

    /** Starts the countdown. No-op if already armed. */
    public void arm() {
        if (remaining < 0) remaining = settleTicks;
    }

    /** Decrements the timer. Returns true exactly once when the timer fires. Resets to idle after firing. */
    public boolean tick() {
        if (remaining < 0) return false;
        remaining--;
        if (remaining == 0) { remaining = -1; return true; }
        return false;
    }

    public void reset() {
        remaining = -1;
    }

    public boolean isArmed() {
        return remaining >= 0;
    }
}
