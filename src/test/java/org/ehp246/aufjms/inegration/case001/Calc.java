package org.ehp246.aufjms.inegration.case001;

import org.ehp246.aufjms.annotation.ByMsg;

@ByMsg("calc.request")
interface Calc {
	int inc(int i);

	void add(int i, int j);

	void mem(int i);
}
