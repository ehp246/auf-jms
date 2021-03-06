package me.ehp246.aufjms.global.case002.bymsg;

import me.ehp246.aufjms.api.annotation.ByMsg;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://me.ehp246.aufjms.request")
@OfType("Calculator")
public interface Counter {
	@Invoking("setMem")
	public Void set(int i);

	@Invoking("addMem")
	public int add(int i);

	@Invoking("getMem")
	public int get();
}
