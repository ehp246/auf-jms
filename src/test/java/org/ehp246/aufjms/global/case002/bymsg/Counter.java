package org.ehp246.aufjms.global.case002.bymsg;

import org.ehp246.aufjms.api.annotation.ByMsg;
import org.ehp246.aufjms.api.annotation.Invoking;
import org.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://org.ehp246.aufjms.request")
@OfType("Calculator")
public interface Counter {
	@Invoking("setMem")
	public Void set(int i);

	@Invoking("addMem")
	public int add(int i);

	@Invoking("getMem")
	public int get();
}
