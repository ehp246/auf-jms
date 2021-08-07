package me.ehp246.aufjms.core.endpoint.case002;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;

/**
 * This class should fail the scan because two same methods have the same
 * Invoking name.
 *
 * @author Lei Yang
 *
 */
@ForJms
public class Case002 {

    @Invoke
    public void m001() {
    }

    @Invoke
    public void m001(final int i) {
    }

}
