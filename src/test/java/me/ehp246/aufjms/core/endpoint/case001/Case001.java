package me.ehp246.aufjms.core.endpoint.case001;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;

/**
 * @author Lei Yang
 *
 */
@ForJms
public class Case001 {

    @Invoke
    public void m001() {
    }

    @Invoke
    public void m003() {
    }

    @Invoke("m001-1")
    public void m001(final int i) {
    }

    @Invoke("m002")
    public void m002(final int i) {
    }

    /**
     * Should not be registered
     */
    public void m002() {
    }
}
