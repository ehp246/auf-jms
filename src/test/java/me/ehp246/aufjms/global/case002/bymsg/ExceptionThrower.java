package me.ehp246.aufjms.global.case002.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = "queue://me.ehp246.aufjms.request")
public interface ExceptionThrower {
    @Invoking("throw001")
    Void throw001();

    @Invoking("throw002")
    Void throw002() throws Exception;

    @Invoking("throw003")
    Void throw003();
}
