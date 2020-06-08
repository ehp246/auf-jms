package org.ehp246.demo.case001;

import org.ehp246.aufjms.annotation.ByMsg;

@ByMsg("calc.request")
public interface Calc {
	int inc(int i);
	void add(int i, int j);
	void mem(int i);
}
