package me.ehp246.aufjms.core.inbound.invokableresolvercase.error.case05;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * This class should fail scan because it's abstract and yet not BEAN-scoped.
 *
 * @author Lei Yang
 *
 */
@ForJmsType("Case05")
public abstract class ErrorCase05 {
    @Invoking
    public void m001() {

    }
}
