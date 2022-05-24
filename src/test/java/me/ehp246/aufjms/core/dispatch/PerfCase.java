package me.ehp246.aufjms.core.dispatch;

import java.util.List;
import java.util.Map;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
interface PerfCase {
    void m01();

    void m02(Map<String, String> map);

    void m03(@OfType final String type);

    void m04(@OfType final String type, @OfCorrelationId String id, @OfProperty Map<String, String> properties,
            List<String> payload);
}
