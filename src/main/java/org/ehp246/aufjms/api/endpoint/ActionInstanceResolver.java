package org.ehp246.aufjms.api.endpoint;

import java.util.List;

import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ActionInstanceResolver {
	List<ResolvedInstance> resolve(Msg msg);
}
