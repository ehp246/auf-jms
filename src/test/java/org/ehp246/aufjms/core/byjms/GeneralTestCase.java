package org.ehp246.aufjms.core.byjms;

import org.ehp246.aufjms.annotation.ByMsg;

public class GeneralTestCase {
	@ByMsg
	interface Case001 {
		void m001();

		default int inc(int i) {
			return ++i;
		}
	}

}
