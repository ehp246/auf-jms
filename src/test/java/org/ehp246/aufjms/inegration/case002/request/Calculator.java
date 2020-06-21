package org.ehp246.aufjms.inegration.case002.request;

import java.util.concurrent.atomic.AtomicReference;

import org.ehp246.aufjms.annotation.Executing;
import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.springframework.stereotype.Service;

/**
 * @author Lei Yang
 *
 */
@Service
@ForMsg(scope = InstanceScope.BEAN)
public class Calculator {
	public AtomicReference<Integer> mem = new AtomicReference<>();

	public void setMem(int i) {
		this.mem.set(i);
	}

	public int addMem(int i) {
		return this.mem.get() + i;
	}

	@Executing
	public int add(int i, int j) {
		return i + j;
	}
}
