package org.ehp246.demo.case002.calc;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Executing;

@ForMsg
public class Calc {
	private int mem = 0;

	public void mem(int i) {
		mem = i;
	}

	@Executing
	public int add(int i, int j) {
		return i + j;
	}

	@Executing
	public int addMem(int i) {
		return mem + i;
	}
}
