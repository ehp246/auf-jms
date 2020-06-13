package org.ehp246.aufjms.api.jms;

import java.util.function.Supplier;

import javax.jms.Destination;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MessagePortProvider {
	MessagePort get(Supplier<Destination> destination);
}
