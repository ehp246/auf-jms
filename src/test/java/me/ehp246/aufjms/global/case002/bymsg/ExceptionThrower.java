package me.ehp246.aufjms.global.case002.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.Invoke;

/**
 * @author Lei Yang
 *
 */
@ByJms(destination = "queue://me.ehp246.aufjms.request")
public interface ExceptionThrower {
    @Invoke("throw001")
    Void throw001();

    @Invoke("throw002")
    Void throw002() throws Exception;

    @Invoke("throw003")
    Void throw003();
}
