package me.ehp246.aufjms.core.inbound;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.ErrorHandler;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.MsgConsumer;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * Creates {@linkplain InboundEndpoint} beans.
 *
 * @author Lei Yang
 * @since 1.0
 * @see AnnotatedInboundEndpointRegistrar
 * @see EnableForJms
 */
public final class InboundEndpointFactory {
    private final PropertyResolver propertyResolver;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final DefaultInvocableScanner invocableScanner;

    public InboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final PropertyResolver propertyResolver) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.propertyResolver = propertyResolver;
        this.invocableScanner = new DefaultInvocableScanner(propertyResolver);
    }

    @SuppressWarnings("unchecked")
    public InboundEndpoint newInstance(final Map<String, Object> inboundAttributes, final Set<String> scanPackages,
            final String beanName, final String defaultConsumerName) {
        final var defaultConsumer = Optional.ofNullable(defaultConsumerName).map(propertyResolver::resolve)
                .filter(OneUtil::hasValue).map(name -> autowireCapableBeanFactory.getBean(name, MsgConsumer.class))
                .orElse(null);

        final var fromAttribute = (Map<String, Object>) inboundAttributes.get("value");

        final var subAttribute = (Map<String, Object>) fromAttribute.get("sub");

        final var atName = propertyResolver.resolve(fromAttribute.get("value").toString());
        final var atType = (DestinationType) (fromAttribute.get("type"));

        final InboundEndpoint.From from = new InboundEndpointRecord.From(
                atType == DestinationType.TOPIC ? At.toTopic(atName) : At.toQueue(atName),
                propertyResolver.resolve(fromAttribute.get("selector").toString()),
                new InboundEndpointRecord.Sub(propertyResolver.resolve(subAttribute.get("name").toString()),
                        (Boolean) (subAttribute.get("shared")), (Boolean) (subAttribute.get("durable"))));

        final int concurrency = Integer
                .parseInt(propertyResolver.resolve(inboundAttributes.get("concurrency").toString()));

        final boolean autoStartup = Boolean
                .parseBoolean(propertyResolver.resolve(inboundAttributes.get("autoStartup").toString()));

        final String connectionFactory = propertyResolver
                .resolve(inboundAttributes.get("connectionFactory").toString());

        final var registery = this.invocableScanner.registeryFrom((Class<?>[]) inboundAttributes.get("register"),
                scanPackages);

        final var invocationListener = Optional.ofNullable(inboundAttributes.get("invocationListener").toString())
                .map(propertyResolver::resolve).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, InvocationListener.class)).orElse(null);

        final var errorHandler = Optional.ofNullable(inboundAttributes.get("errorHandler").toString())
                .map(propertyResolver::resolve).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, ErrorHandler.class)).orElse(null);

        return new InboundEndpointRecord(from, registery, concurrency, beanName, autoStartup, connectionFactory,
                invocationListener, defaultConsumer, (int) inboundAttributes.get("sessionMode"), errorHandler);
    }
}
