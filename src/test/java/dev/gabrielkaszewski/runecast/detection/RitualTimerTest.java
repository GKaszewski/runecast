package dev.gabrielkaszewski.runecast.detection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RitualTimerTest {
    @Test
    void idleTimerDoesNotFire() {
        RitualTimer t = new RitualTimer(3);
        assertFalse(t.isArmed());
        assertFalse(t.tick());
        assertFalse(t.tick());
    }

    @Test
    void firesAfterSettleTicks() {
        RitualTimer t = new RitualTimer(3);
        t.arm();
        assertTrue(t.isArmed());
        assertFalse(t.tick()); // tick 2
        assertFalse(t.tick()); // tick 1
        assertTrue(t.tick());  // tick 0 → fires
        assertFalse(t.isArmed()); // auto-reset after fire
    }

    @Test
    void resetCancelsArmedTimer() {
        RitualTimer t = new RitualTimer(3);
        t.arm();
        t.tick();
        t.reset();
        assertFalse(t.isArmed());
        assertFalse(t.tick()); // no fire after reset
    }

    @Test
    void armIsIdempotentWhenAlreadyArmed() {
        RitualTimer t = new RitualTimer(3);
        t.arm();
        t.tick(); // countdown now at 2
        t.arm();  // should NOT restart countdown
        assertFalse(t.tick()); // still at 2
        assertTrue(t.tick());  // fires at 0 (was 1 after previous tick)
    }
}
