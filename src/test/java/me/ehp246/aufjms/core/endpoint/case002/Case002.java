package me.ehp246.aufjms.core.endpoint.case002;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * This class should fail the scan because two same methods have the same
 * Invoking name.
 *
 * @author Lei Yang
 *
 */
@ForJms
public class Case002 {

    @Invoking
    public void m001() {
    }

    @Invoking
    public void m001(final int i) {
    }

}