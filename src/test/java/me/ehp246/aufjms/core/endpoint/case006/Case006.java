package me.ehp246.aufjms.core.endpoint.case006;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * Should scan as a bean.
 *
 * @author Lei Yang
 *
 */
@ForJms(scope = InstanceScope.BEAN)
public interface Case006 {
    @Invoke
    void m001();
}
