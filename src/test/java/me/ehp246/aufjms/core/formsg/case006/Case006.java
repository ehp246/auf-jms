package me.ehp246.aufjms.core.formsg.case006;

import me.ehp246.aufjms.api.annotation.ForMsg;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * Should scan as a bean.
 *
 * @author Lei Yang
 *
 */
@ForMsg(scope = InstanceScope.BEAN)
public interface Case006 {
    @Invoking
    void m001();
}
