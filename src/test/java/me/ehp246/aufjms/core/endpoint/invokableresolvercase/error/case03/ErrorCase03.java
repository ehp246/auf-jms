package me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case03;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * This class should not be scanned because of duplicate named invocations.
 * 
 * @author Lei Yang
 *
 */
@ForJmsType("Case03")
public class ErrorCase03 {
    @Invoking("m001")
    public void m001() {
    }

    @Invoking("m001")
    public void m001(int i) {
    }

}
