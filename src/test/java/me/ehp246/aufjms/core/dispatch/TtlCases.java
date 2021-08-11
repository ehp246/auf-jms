package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.annotation.OfTtl;

/**
 * @author Lei Yang
 *
 */
class TtlCases {
    interface Case01 {
        void get();

        @OfTtl
        void getTtl01();

        @OfTtl("PT10S")
        void getTtl02();

        @OfTtl("SSS")
        void getTtl03();

    }
}
