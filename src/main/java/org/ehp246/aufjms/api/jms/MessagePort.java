package org.ehp246.aufjms.api.jms;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MessagePort {
	Msg accept(MessageSupplier msgSupplier);
}
