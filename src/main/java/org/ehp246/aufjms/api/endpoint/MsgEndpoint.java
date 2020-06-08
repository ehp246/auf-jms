package org.ehp246.aufjms.api.endpoint;

import javax.jms.Message;

/**
 * 
 * @author Lei Yang
 *
 */
public interface MsgEndpoint {
	String getId();
	
	String getDestinationName();

    void onMessage(Message message);
}
