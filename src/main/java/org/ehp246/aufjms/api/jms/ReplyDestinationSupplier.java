package org.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ReplyDestinationSupplier {
	Destination get();
}