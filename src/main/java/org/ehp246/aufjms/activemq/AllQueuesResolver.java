package org.ehp246.aufjms.activemq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.ehp246.aufjms.api.jms.GoToNameResolver;

/**
 * 
 * @author Lei Yang
 *
 */
public class AllQueuesResolver implements GoToNameResolver {
	private final Map<String, Destination> known = new ConcurrentHashMap<>();

	@Override
	public Destination resolve(String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Un-specified destination name");
		}

		return known.computeIfAbsent(name, key -> new ActiveMQQueue(key));
	}

}
