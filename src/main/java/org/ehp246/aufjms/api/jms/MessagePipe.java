package org.ehp246.aufjms.api.jms;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MessagePipe {
	Msg take(MessageSupplier msgSupplier);
}
