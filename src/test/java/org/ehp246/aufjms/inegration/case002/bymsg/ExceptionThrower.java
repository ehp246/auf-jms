package org.ehp246.aufjms.inegration.case002.bymsg;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://org.ehp246.aufjms.inegration.case002.Case002Configuration.request")
public interface ExceptionThrower {
	@Invoking("throw001")
	Void throw001();

	@Invoking("throw002")
	Void throw002() throws Exception;
}
