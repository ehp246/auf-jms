package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
interface TtlTestCases {
    @ByJms(destination = "")
    interface Case001 {
        void m001();
    }

    @ByJms(destination = "", ttl = "PT500S")
    interface Case002 {
        void m001();
    }
}
