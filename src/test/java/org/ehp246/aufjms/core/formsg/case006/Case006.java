package org.ehp246.aufjms.core.formsg.case006;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.api.endpoint.InstanceScope;

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
