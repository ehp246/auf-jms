package org.ehp246.aufjms.api.endpoint;

import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ExecutingInstanceResolver {
	ResolvedInstance resolve(Msg msg);
}
