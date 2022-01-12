package me.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.AtDestination;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundEndpointFactory {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public InboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    public InboundEndpoint newInstance(final AtDestination at, final Set<String> scanPackages, final String concurrency,
            final String name, final String autoStartup) {
        return new InboundEndpoint() {
            private final ExecutableResolver resolver = new AutowireCapableInstanceResolver(autowireCapableBeanFactory,
                    DefaultInvokableResolver.registeryFrom(scanPackages));

            @Override
            public AtDestination at() {
                return at;
            }

            @Override
            public ExecutableResolver resolver() {
                return resolver;
            }

            @Override
            public String concurrency() {
                return concurrency;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String autoStartup() {
                return autoStartup;
            }

        };
    }
}
