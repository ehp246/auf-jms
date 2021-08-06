package me.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.AtEndpoint;

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

    public AtEndpoint newEndpoint(final String destination, final Set<String> scanPackages) {
        return new AtEndpoint() {
            private final ExecutableResolver resolver = new AutowireCapableInstanceResolver(autowireCapableBeanFactory,
                    DefaultInvokableResolver.registeryFrom(scanPackages));

            @Override
            public String getDestinationName() {
                return destination;
            }

            @Override
            public ExecutableResolver getResolver() {
                return resolver;
            }

        };
    }
}
