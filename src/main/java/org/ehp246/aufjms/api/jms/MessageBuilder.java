package org.ehp246.aufjms.api.jms;

import javax.jms.Message;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MessageBuilder {
	Message build(MsgSinkContext sinkContext);
}
