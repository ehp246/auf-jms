package me.ehp246.aufjms.core.inbound.invokableresolvercase.error.case02;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * This class should fail the scan because two same methods have the same
 * Invoking name.
 *
 * @author Lei Yang
 *
 */
@ForJmsType("Case02")
public class ErrorCase02 {

    @Invoking
    public void m001() {
    }

    @Invoking
    public void m001(final int i) {
    }

}
