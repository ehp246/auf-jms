package me.ehp246.aufjms.core.dispatch;

import java.util.Map;

import me.ehp246.aufjms.api.annotation.OfProperty;

/**
 * @author Lei Yang
 *
 */
class PropertyCases {
    static interface Case01 {
        void m01(@OfProperty Map<String, Object> map1, @OfProperty Map<String, Object> map2);

        void m01(@OfProperty String name);

        void m01(@OfProperty("ID") String id, @OfProperty("SEQ") int seq, @OfProperty Map<String, Object> map);
    }
}
