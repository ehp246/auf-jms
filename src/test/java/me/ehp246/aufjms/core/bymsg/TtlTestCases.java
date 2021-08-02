package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
interface TtlTestCases {
    @ByJms("")
    interface Case001 {
        void m001();
    }

    @ByJms(value = "", ttl = 500)
    interface Case002 {
        void m001();
    }
}
