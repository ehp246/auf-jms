package org.ehp246.aufjms.global.case002.bymsg;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://org.ehp246.aufjms.request")
public interface ExceptionThrower {
	@Invoking("throw001")
	Void throw001();

	@Invoking("throw002")
	Void throw002() throws Exception;

	@Invoking("throw003")
	Void throw003();
}
