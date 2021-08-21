package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case06;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * Should scan as a bean.
 *
 * @author Lei Yang
 *
 */
@ForJms(value = "Case06", scope = InstanceScope.BEAN)
public interface Case06 {
    @Invoking
    void m001();
}
