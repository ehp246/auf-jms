package org.ehp246.aufjms.integration.case002.bymsg;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://org.ehp246.aufjms.integration.case002.AppConfiguration.request")
@OfType("Calculator")
public interface Counter {
	@Invoking("setMem")
	public Void set(int i);

	@Invoking("addMem")
	public int add(int i);

	@Invoking("getMem")
	public int get();
}
