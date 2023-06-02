package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

class ByJmsProxyFactoryTestCases {
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

    @ByJms(value = @To("queue"), requestTimeout = "PT1S")
    interface FutureMapCase01 {
        int get();
    }

    @ByJms(value = @To("queue"), requestTimeout = "${local.timeout:}")
    interface TimeoutCase01 {
        int get();
    }
}
