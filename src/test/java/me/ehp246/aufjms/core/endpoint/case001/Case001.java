package me.ehp246.aufjms.core.endpoint.case001;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJms
public class Case001 {

    @Invoking
    public void m001() {
    }

    @Invoking
    public void m003() {
    }

    @Invoking("m001-1")
    public void m001(final int i) {
    }

    @Invoking("m002")
    public void m002(final int i) {
    }

    /**
     * Should not be registered
     */
    public void m002() {
    }
}
