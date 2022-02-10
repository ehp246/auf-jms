package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

interface DestinationTestCase {

    @ByJms(@To("test.inbox"))
    interface Case001 {
        void m001();
    }

    @ByJms(@To(""))
    interface Case002 {
        void m001();
    }

    @ByJms(@To("test.inbox"))
    interface Case003 {
        void m001();
    }
}
