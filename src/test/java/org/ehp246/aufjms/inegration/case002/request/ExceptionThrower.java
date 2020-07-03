package org.ehp246.aufjms.inegration.case002.request;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.api.exception.ForMsgExecutionException;

/**
 * @author Lei Yang
 *
 */
@ForMsg
public class ExceptionThrower {
	@Invoking("throw001")
	public void throw001() {
		throw new RuntimeException("Throw 001");
	}

	@Invoking("throw002")
	public void throw002() throws Exception {
		throw new Exception("Throw 002");
	}

	@Invoking("throw003")
	public void throw003() {
		throw new ForMsgExecutionException(3, "Throw 003");
	}
}
