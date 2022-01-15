package me.ehp246.aufjms.core.configuration;

import javax.jms.ConnectionFactory;

import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;

/**
 * @author Lei Yang
 * @since
 */
final class DefaultConectionFactoryProvider implements ConnectionFactoryProvider {
    private final ConnectionFactory connectionFactory;

    DefaultConectionFactoryProvider(final ConnectionFactory connectionFactory) {
        super();
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ConnectionFactory get(String name) {
        return this.connectionFactory;
    }
}
