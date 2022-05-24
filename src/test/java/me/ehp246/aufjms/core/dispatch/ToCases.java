package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
interface ToCases {
    @ByJms(@To("q1"))
    interface ToCase01 {
        void m01();
    }
}
