package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.Received;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ExecutableResolver {
    ResolvedExecutable resolve(Received msg);
}
