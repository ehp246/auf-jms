package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;

class TestCases {
    @ByJms(value = @ByJms.At("queue1"), ttl = "PT10S", context = "SB1")
    interface Case01 {
        void m001();

        default int inc(int i) {
            return ++i;
        }
    }

}
