package org.ehp246.demo.case002.calc;

import org.ehp246.aufjms.annotation.ForMsg;

@ForMsg
public class Calc {
	private int mem = 0;
	
	void mem(int i) {
		mem = i;
	}
	
	int add(int i, int j) {
		return i + j;
	}
	
	int addMem(int i) {
		return mem + i;
	}
}
