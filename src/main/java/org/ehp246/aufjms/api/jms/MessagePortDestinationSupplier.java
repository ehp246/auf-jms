package org.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * @author Lei Yang
 *
 */
public interface MessagePortDestinationSupplier {
	Destination getTo();

	default Destination getReplyTo() {
		return null;
	}
}
