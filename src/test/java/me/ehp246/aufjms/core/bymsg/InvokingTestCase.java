package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.Invoke;

/**
 * @author Lei Yang
 *
 */
class InvokingTestCase {
    @ByJms(destination = "")
    interface Case001 {
        // Invoking by method name
        void m001();

        // Invoking by method name
        void m002();

        // Invoking by method name
        @Invoke
        void m003();

        @Invoke("m003-1")
        void m003(int i);
    }
}
