package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case05;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * This class should fail scan because it's abstract and yet not BEAN-scoped.
 *
 * @author Lei Yang
 *
 */
@ForJms
public abstract class Case05 {
    @Invoking
    public void m001() {

    }
}
