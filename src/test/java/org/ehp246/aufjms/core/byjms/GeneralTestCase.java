package org.ehp246.aufjms.core.byjms;

public class GeneralTestCase {
	interface Case001 {
		void m001();
		
		default int inc(int i) {
			return ++i;
		}
	}

}
