package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByMsg;

/**
 * @author Lei Yang
 *
 */
interface TtlTestCases {
	@ByMsg("")
	interface Case001 {
		void m001();
	}

	@ByMsg(value = "", ttl = 500)
	interface Case002 {
		void m001();
	}
}
