package me.ehp246.aufjms.core.dispatch;

import java.time.temporal.TemporalAccessor;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
class BodyCases {
    static interface Case01 {
        void m01();

        void m02(Map<String, String> map);

        void m02(@OfType String type, Map<String, String> map);

        void m02(@OfType String type, @OfProperty String property, Map<String, String> map);

        void m02(Map<String, String> map, @OfType String type, @OfTtl String ttl);

        void m03(@OfType String type, @OfCorrelationId String id);

        void m03(@OfType String type, @OfType String type2, @OfDelay String delay);

        void m04(@Nonnull @Nullable TemporalAccessor accessor);
    }
}
