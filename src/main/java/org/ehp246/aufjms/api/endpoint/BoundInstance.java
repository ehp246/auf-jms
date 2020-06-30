package org.ehp246.aufjms.api.endpoint;

import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
public interface BoundInstance {
	Msg getMsg();

	ResolvedExecutable getResolvedInstance();
}
