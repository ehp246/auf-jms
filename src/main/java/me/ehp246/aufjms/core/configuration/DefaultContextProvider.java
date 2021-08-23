package me.ehp246.aufjms.core.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;

import me.ehp246.aufjms.api.jms.ContextProvider;

/**
 * @author Lei Yang
 * @since
 */
final class DefaultContextProvider implements ContextProvider, AutoCloseable {
    private final ConnectionFactory connectionFactory;
    private final Map<String, JMSContext> map = new ConcurrentHashMap<>();

    DefaultContextProvider(final ConnectionFactory connectionFactory) {
        super();
        this.connectionFactory = connectionFactory;
    }

    @Override
    public JMSContext get(String name) {
        return map.computeIfAbsent(name == null ? "" : name, key -> connectionFactory.createContext());
    }

    @Override
    public void close() {
        map.entrySet().stream().map(Map.Entry::getValue).forEach(ctx -> ctx.close());
    }
}
