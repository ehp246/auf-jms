package org.ehp246.aufjms.api.endpoint;

/**
 * 
 * @author Lei Yang
 *
 */
public interface MsgEndpoint {
	String getDestinationName();

	ActionInstanceResolver getResolver();
}
