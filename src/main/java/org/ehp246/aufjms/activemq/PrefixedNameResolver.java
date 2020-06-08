package org.ehp246.aufjms.activemq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
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
	public Destination resolve(String prefixedName) {
		if (prefixedName == null || prefixedName.isBlank()) {
			throw new IllegalArgumentException("Un-specified destination name");
		}

		if (!prefixedName.startsWith(TOPIC_PREFIX) && !prefixedName.startsWith(QUEUE_PREFIX)) {
			throw new IllegalArgumentException("Un-known destination prefix");
		}

		return known.computeIfAbsent(prefixedName,
				key -> new ActiveMQQueue(key.startsWith(TOPIC_PREFIX) ? key.replaceFirst(TOPIC_PREFIX, "")
						: key.replaceFirst(QUEUE_PREFIX, "")));
	}
}
