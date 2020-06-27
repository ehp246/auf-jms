package org.ehp246.aufjms.enableformsg.case001;

import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.ForMsg;

@ForMsg
public class Calc {
	private int mem = 0;

	public void mem(int i) {
		mem = i;
	}

	@Invoking
	public int add(int i, int j) {
		return i + j;
	}

	public int addMem(int i) {
		return mem + i;
	}
}
