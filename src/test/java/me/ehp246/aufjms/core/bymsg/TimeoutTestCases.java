package me.ehp246.aufjms.core.bymsg;

import java.util.concurrent.TimeoutException;

import me.ehp246.aufjms.api.annotation.ByMsg;

/**
 * @author Lei Yang
 *
 */
interface TimeoutTestCases {
	@ByMsg("")
	interface Case001 {
		Void m001();

		Void m002() throws TimeoutException;
	}

	@ByMsg(value = "", timeout = 500)
	interface Case002 {
		Void m001();
	}
}
