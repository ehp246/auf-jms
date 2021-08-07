package me.ehp246.aufjms.global.case002.request;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;
import me.ehp246.aufjms.api.exception.ForMsgExecutionException;

/**
 * @author Lei Yang
 *
 */
@ForJms
public class ExceptionThrower {
    @Invoke("throw001")
    public void throw001() {
        throw new RuntimeException("Throw 001");
    }

    @Invoke("throw002")
    public void throw002() throws Exception {
        throw new Exception("Throw 002");
    }

    @Invoke("throw003")
    public void throw003() {
        throw new ForMsgExecutionException(3, "Throw 003");
    }
}
