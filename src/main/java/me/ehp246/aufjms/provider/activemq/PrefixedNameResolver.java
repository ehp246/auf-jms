package me.ehp246.aufjms.provider.activemq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.core.env.Environment;

import me.ehp246.aufjms.api.jms.DestinationNameResolver;

/**
 *
 * @author Lei Yang
 *
 */
public class PrefixedNameResolver implements DestinationNameResolver {
    public static final String TOPIC_PREFIX = "topic://";
    public static final String QUEUE_PREFIX = "queue://";

    private final Map<String, Destination> known = new ConcurrentHashMap<>();
    private final Environment env;

    public PrefixedNameResolver(final Environment env) {
        super();
        this.env = env;
    }

    @Override
    public Destination resolve(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Un-specified destination name");
        }

        return known.computeIfAbsent(name, this::toDestination);
    }

    private Destination toDestination(final String name) {
        final var resolvedName = env.resolveRequiredPlaceholders(name);

        if (resolvedName.startsWith(TOPIC_PREFIX)) {
            return new ActiveMQTopic(resolvedName.replaceFirst(TOPIC_PREFIX, ""));
        } else {
            return new ActiveMQQueue(resolvedName.replaceFirst(QUEUE_PREFIX, ""));
        }
    }
}
