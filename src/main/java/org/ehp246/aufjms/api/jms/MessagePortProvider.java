package org.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MessagePortProvider {
	MsgPort get(MessagePortDestinationSupplier destinationSupplier);
}
