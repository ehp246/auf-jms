package in.ehp246.aufjms.global.case002.request;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForMsg;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

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
