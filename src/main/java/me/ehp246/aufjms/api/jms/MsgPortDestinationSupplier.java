package me.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * @author Lei Yang
 *
 */
public interface MsgPortDestinationSupplier {
	Destination getTo();

	default Destination getReplyTo() {
		return null;
	}
}
