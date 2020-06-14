package org.ehp246.aufjms.activemq;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQTopic;
import org.ehp246.aufjms.api.jms.RespondToDestinationSupplier;

/**
 * 
 * @author Lei Yang
 *
 */
public class SingleTopicReplyToSupplier implements RespondToDestinationSupplier {
	private final Destination replyTo;

	public SingleTopicReplyToSupplier(String name) {
		super();
		this.replyTo = new ActiveMQTopic(name);
	}

	@Override
	public Destination get() {
		return this.replyTo;
	}

}
