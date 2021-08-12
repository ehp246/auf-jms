package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
class InvokingTestCase {
    @ByJms(value = "")
    interface Case001 {
        // Invoking by method name
        void m001();

        // Invoking by method name
        void m002();

        // Invoking by method name
        @Invoking
        void m003();

        @Invoking("m003-1")
        void m003(int i);
    }
}
