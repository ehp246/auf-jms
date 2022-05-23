package me.ehp246.aufjms.core.dispatch;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;

/**
 * @author Lei Yang
 *
 */
interface CorrelationIdCases {
    static interface Case01 {
        void m01();

        void m01(@OfCorrelationId String id);

        void m02(@OfCorrelationId String id1, @OfCorrelationId String id2);

        void m03(@OfCorrelationId Instant id);
    }
}
