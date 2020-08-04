package in.ehp246.aufjms.global.case002.bymsg;

import in.ehp246.aufjms.api.annotation.ByMsg;
import in.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://in.ehp246.aufjms.request")
public interface Add {
	@OfType("Calculator")
	public int add(int i, int j);
}
