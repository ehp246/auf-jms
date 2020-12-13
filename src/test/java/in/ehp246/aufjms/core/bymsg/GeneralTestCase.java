package in.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.ByMsg;

public class GeneralTestCase {
	@ByMsg("")
	interface Case001 {
		void m001();

		default int inc(int i) {
			return ++i;
		}
	}

}
