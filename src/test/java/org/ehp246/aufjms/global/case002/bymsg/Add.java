package org.ehp246.aufjms.global.case002.bymsg;

import org.ehp246.aufjms.api.annotation.ByMsg;
import org.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://org.ehp246.aufjms.request")
public interface Add {
	@OfType("Calculator")
	public int add(int i, int j);
}
