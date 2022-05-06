package me.ehp246.aufjms.core.endpoint;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.CompletedInvocationListener;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.FailedInvocationInterceptor;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

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

    @SuppressWarnings("unchecked")
    public InboundEndpoint newInstance(final Map<String, Object> inboundAttributes, final Set<String> scanPackages,
            final String beanName) {
        final var fromAttribute = (Map<String, Object>) inboundAttributes.get("value");
        final Map<String, Object> subAttribute = (Map<String, Object>) fromAttribute.get("sub");

        final var atName = propertyResolver.resolve(fromAttribute.get("value").toString());
        final var atType = (DestinationType) (fromAttribute.get("type"));

        final InboundEndpoint.From from = new InboundEndpointRecord.From(
                atType == DestinationType.TOPIC ? At.toTopic(atName) : At.toQueue(atName),
                propertyResolver.resolve(fromAttribute.get("selector").toString()),
                new InboundEndpointRecord.Sub(propertyResolver.resolve(subAttribute.get("value").toString()),
                        (Boolean) (subAttribute.get("shared")), (Boolean) (subAttribute.get("durable"))));

        final int concurrency = Integer
                .parseInt(propertyResolver.resolve(inboundAttributes.get("concurrency").toString()));
        final boolean autoStartup = Boolean
                .parseBoolean(propertyResolver.resolve(inboundAttributes.get("autoStartup").toString()));
        final String connectionFactory = propertyResolver
                .resolve(inboundAttributes.get("connectionFactory").toString());
        final ExecutableResolver resolver = new AutowireCapableExecutableResolver(autowireCapableBeanFactory,
                DefaultInvokableResolver.registeryFrom(scanPackages));
        final FailedInvocationInterceptor failedInterceptor = Optional
                .ofNullable(inboundAttributes.get("failedInvocationInterceptor").toString())
                .map(propertyResolver::resolve).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, FailedInvocationInterceptor.class)).orElse(null);
        final CompletedInvocationListener completedConsumer = Optional
                .ofNullable(inboundAttributes.get("completedInvocationListener").toString())
                .map(propertyResolver::resolve).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, CompletedInvocationListener.class)).orElse(null);

        return new InboundEndpointRecord(from, resolver, concurrency, beanName, autoStartup, connectionFactory,
                completedConsumer, failedInterceptor);
    }
}
