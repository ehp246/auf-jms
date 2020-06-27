package org.ehp246.aufjms.inegration.case002.bymsg;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://org.ehp246.aufjms.inegration.case002.Case002Configuration.request")
@OfType("Calculator")
public interface Countor {
	@Invoking("setMem")
	public void set(int i);

	@Invoking("addMem")
	public int inc();

	@Invoking("getMem")
	public int get();
}
