package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 * @see EnableByJmsRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata,
 *      org.springframework.beans.factory.support.BeanDefinitionRegistry)
 */
public final class EnableByJmsBeanFactory {
    private final PropertyResolver propertyResolver;

    public EnableByJmsBeanFactory(final PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    public EnableByJmsConfig enableByJmsConfig(final List<Class<?>> scan, final String ttl, final String delay,
            final List<String> dispatchFns, final String requestReplyToValue,
            final DestinationType requestReplyToType) {
        return new EnableByJmsConfig(scan,
                Optional.ofNullable(propertyResolver.resolve(ttl)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                Optional.ofNullable(propertyResolver.resolve(delay)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                dispatchFns,
                Optional.ofNullable(propertyResolver.resolve(requestReplyToValue)).filter(OneUtil::hasValue).map(
                        value -> requestReplyToType == DestinationType.TOPIC ? At.toTopic(value) : At.toQueue(value))
                        .orElse(null));
    }
}
