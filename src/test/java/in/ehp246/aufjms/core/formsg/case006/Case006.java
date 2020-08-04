package in.ehp246.aufjms.core.formsg.case006;

import in.ehp246.aufjms.api.annotation.ForMsg;
import in.ehp246.aufjms.api.annotation.Invoking;
import in.ehp246.aufjms.api.endpoint.InstanceScope;

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
