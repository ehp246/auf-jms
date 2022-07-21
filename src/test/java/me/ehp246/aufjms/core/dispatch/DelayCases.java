package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.time.Instant;

import me.ehp246.aufjms.api.annotation.OfDelay;

/**
 * @author Lei Yang
 *
 */
class DelayCases {
    static interface Case01 {
        @OfDelay("PT2S")
        void m01(@OfDelay Duration delay);

        @OfDelay("PT2S")
        void m01();

        void m01(@OfDelay String delay);

        void m01(@OfDelay Instant delay);

        @OfDelay
        void m03();

        void m04(@OfDelay Duration delay);
    }
}
