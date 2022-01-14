package me.ehp246.aufjms.core.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;

import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;

/**
 * @author Lei Yang
 * @since
 */
final class DefaultConectionFactoryProvider implements ConnectionFactoryProvider, AutoCloseable {
    private final ConnectionFactory connectionFactory;
    private final Map<String, JMSContext> ctxMap = new ConcurrentHashMap<>();

    DefaultConectionFactoryProvider(final ConnectionFactory connectionFactory) {
        super();
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ConnectionFactory get(String name) {
        return this.connectionFactory;
    }

    public JMSContext getContext(String name) {
        return ctxMap.computeIfAbsent(name == null ? "" : name, key -> connectionFactory.createContext());
    }

    @Override
    public void close() {
        ctxMap.entrySet().stream().map(Map.Entry::getValue).forEach(ctx -> ctx.close());
    }
}
