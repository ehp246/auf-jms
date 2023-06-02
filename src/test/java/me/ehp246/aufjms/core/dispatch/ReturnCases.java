package me.ehp246.aufjms.core.dispatch;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
interface ReturnCases {
    @ByJms(@To("q"))
    interface VoidCase01 {
        void m01();

        Void m02();
    }

    @ByJms(@To("q"))
    interface ReturnCase01 {
        int m01();

        Integer m02();

        Instant m03();

        Person m04();
    }

    record Person(String firstName, String lastName) {
    }
}
