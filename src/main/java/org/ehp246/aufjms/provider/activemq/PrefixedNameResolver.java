package org.ehp246.aufjms.provider.activemq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;

/**
 *
 * @author Lei Yang
 *
 */
public class PrefixedNameResolver implements DestinationNameResolver {
	public static final String TOPIC_PREFIX = "topic://";
	public static final String QUEUE_PREFIX = "queue://";

	private final Map<String, Destination> known = new ConcurrentHashMap<>();

	@Override
	public Destination resolve(final String prefixedName) {
		if (prefixedName == null || prefixedName.isBlank()) {
			throw new IllegalArgumentException("Un-specified destination name");
		}

		return known.computeIfAbsent(prefixedName, this::toDestination);
	}

	private Destination toDestination(final String prefixedName) {
		if (prefixedName.startsWith(TOPIC_PREFIX)) {
			return new ActiveMQTopic(prefixedName.replaceFirst(TOPIC_PREFIX, ""));
		} else {
			return new ActiveMQQueue(prefixedName.replaceFirst(QUEUE_PREFIX, ""));
		}
	}
}
