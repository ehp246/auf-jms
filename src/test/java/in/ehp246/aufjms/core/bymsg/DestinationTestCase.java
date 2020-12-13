package in.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByMsg;

public interface DestinationTestCase {

	@ByMsg(value = "test.inbox")
	interface Case001 {
		void m001();
	}

	@ByMsg("")
	interface Case002 {
		void m001();
	}

	@ByMsg(value = "test.inbox")
	interface Case003 {
		void m001();
	}
}
