package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

class TestCases {
    @ByJms(value = @To("queue1"), ttl = "PT10S", connectionFactory = "SB1")
    interface Case01 {
        void m001();

        default int inc(int i) {
            return ++i;
        }
    }

    @ByJms(value = @To("queue1"), properties = { "1" })
    interface PropertyCase01 {
        void ping();
    }
}