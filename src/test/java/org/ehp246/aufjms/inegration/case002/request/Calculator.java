package org.ehp246.aufjms.inegration.case002.request;

import java.util.concurrent.atomic.AtomicReference;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Invoking;
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

	@Invoking("setMem")
	public void setMem(int i) {
		this.mem.set(i);
	}

	@Invoking("addMem")
	public int addMem(int i) {
		this.mem.set(this.mem.get() + i);
		return mem.get();
	}

	@Invoking("getMem")
	public int getMem() {
		return this.mem.get();
	}

	@Invoking
	public int add(int i, int j) {
		return i + j;
	}
}
