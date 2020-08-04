package in.ehp246.aufjms.api.endpoint;

import in.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MsgDispatcher {
	void dispatch(final Msg msg);
}
