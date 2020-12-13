package in.ehp246.aufjms.global.case002.request;

import me.ehp246.aufjms.api.annotation.ForMsg;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.exception.ForMsgExecutionException;

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
