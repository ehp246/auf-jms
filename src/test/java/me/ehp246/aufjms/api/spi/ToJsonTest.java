package me.ehp246.aufjms.api.spi;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ToJsonTest {

    @Test
    void from_01() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ToJson.From(Instant.now(), String.class));
    }

    @Test
    void from_02() {
        Assertions.assertDoesNotThrow(() -> new ToJson.From(Instant.now(), TemporalAccessor.class));
    }
}
