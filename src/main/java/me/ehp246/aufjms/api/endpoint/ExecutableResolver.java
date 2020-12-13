package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ExecutableResolver {
	ResolvedExecutable resolve(Msg msg);
}
