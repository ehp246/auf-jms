package me.ehp246.aufjms.core.inbound.invokableresolvercase.case06;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.inbound.InstanceScope;

/**
 * Should scan as a bean.
 *
 * @author Lei Yang
 *
 */
@ForJmsType(value = "Case06", scope = InstanceScope.BEAN)
public interface Case06 {
    @Invoking
    void m001();
}
