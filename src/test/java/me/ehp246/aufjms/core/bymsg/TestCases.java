package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;

class TestCases {
    @ByJms(destination = "queue1", ttl = 10, connection = "SB1")
    interface Case01 {
        void m001();

        default int inc(int i) {
            return ++i;
        }
    }

}
