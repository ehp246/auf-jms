package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;

public interface DestinationTestCase {

    @ByJms(destination = "test.inbox")
    interface Case001 {
        void m001();
    }

    @ByJms(destination = "")
    interface Case002 {
        void m001();
    }

    @ByJms(destination = "test.inbox")
    interface Case003 {
        void m001();
    }
}
