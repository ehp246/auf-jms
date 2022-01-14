package me.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.jms.AtDestinationRecord;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundEndpointFactory {
    private final PropertyResolver propertyResolver;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public InboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final PropertyResolver propertyResolver) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.propertyResolver = propertyResolver;
    }

    public InboundEndpoint newInstance(final String atName, final DestinationType atType,
            final Set<String> scanPackages, final String concurrency,
            final String name, final String autoStartup, final boolean shared, final boolean durable,
            final String subscriptionName, final String connectionFactory) {

        final var at = new AtDestinationRecord(this.propertyResolver.resolve(atName), atType);
        final var autoStart = Boolean.parseBoolean(this.propertyResolver.resolve(autoStartup));
        final var concur = Integer.parseInt(this.propertyResolver.resolve(concurrency));
        final var subName = this.propertyResolver.resolve(subscriptionName);
        final var cfName = this.propertyResolver.resolve(connectionFactory);

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
            public int concurrency() {
                return concur;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean autoStartup() {
                return autoStart;
            }

            @Override
            public boolean shared() {
                return shared;
            }

            @Override
            public boolean durable() {
                return durable;
            }

            @Override
            public String subscriptionName() {
                return subName;
            }

            @Override
            public String connectionFactory() {
                return cfName;
            }
        };
    }
}
