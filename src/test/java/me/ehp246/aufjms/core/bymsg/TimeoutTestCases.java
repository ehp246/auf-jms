package me.ehp246.aufjms.core.bymsg;

import java.util.concurrent.TimeoutException;

import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
interface TimeoutTestCases {
    @ByJms(destination = "")
    interface Case001 {
        Void m001();

        Void m002() throws TimeoutException;
    }

    @ByJms(destination = "", timeout = 500)
    interface Case002 {
        Void m001();
    }
}
