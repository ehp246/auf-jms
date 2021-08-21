package me.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.AtEndpoint;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class AtEndpointFactory {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public AtEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    public AtEndpoint newInstance(final String connection, final String destination, final Set<String> scanPackages,
            final String concurrency, final String name) {
        return new AtEndpoint() {
            private final ExecutableResolver resolver = new AutowireCapableInstanceResolver(autowireCapableBeanFactory,
                    DefaultInvokableResolver.registeryFrom(scanPackages));

            @Override
            public String destination() {
                return destination;
            }

            @Override
            public ExecutableResolver resolver() {
                return resolver;
            }

            @Override
            public String connection() {
                return connection;
            }

            @Override
            public String concurrency() {
                return concurrency;
            }

            @Override
            public String name() {
                return name;
            }

        };
    }
}
