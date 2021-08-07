package me.ehp246.aufjms.core.endpoint.case005;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;

/**
 * This class should fail scan because it's abstract and yet not BEAN-scoped.
 *
 * @author Lei Yang
 *
 */
@ForJms
public abstract class Case005 {
    @Invoke
    public void m001() {

    }
}
