package org.ehp246.aufjms.core.byjms;

import org.ehp246.aufjms.annotation.ByMsg;

public interface DestinationTestCase {

	@ByMsg(value = "test.inbox")
	interface Case001 {
		void m001();
	}

	@ByMsg
	interface Case002 {
		void m001();
	}

	@ByMsg(value = "test.inbox")
	interface Case003 {
		void m001();
	}
}
