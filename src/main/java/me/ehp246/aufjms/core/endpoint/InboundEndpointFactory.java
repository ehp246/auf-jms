package me.ehp246.aufjms.core.endpoint;

import java.util.Objects;
import java.util.Set;

import javax.jms.Destination;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.ContextProvider;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundEndpointFactory {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final ContextProvider ctxProvider;

    public InboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final ContextProvider ctxProvider) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.ctxProvider = Objects.requireNonNull(ctxProvider);
    }

    public InboundEndpoint newInstance(final AtDestination at, final Set<String> scanPackages, final String concurrency,
            final String name) {
        final var jmsContext = this.ctxProvider.get("");
        final var destination = at.type() == DestinationType.QUEUE ? jmsContext.createQueue(at.name())
                : this.ctxProvider.get("").createTopic(name);
        return new InboundEndpoint() {
            private final ExecutableResolver resolver = new AutowireCapableInstanceResolver(autowireCapableBeanFactory,
                    DefaultInvokableResolver.registeryFrom(scanPackages));

            @Override
            public Destination destination() {
                return destination;
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

        };
    }
}
