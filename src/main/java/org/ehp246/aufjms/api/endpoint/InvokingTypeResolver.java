package org.ehp246.aufjms.api.endpoint;

import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvokingTypeResolver {
	ResolvedInstanceType resolve(Msg msg);
}
