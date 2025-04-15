package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.spi.ExpressionResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 * @see EnableByJmsRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata,
 *      org.springframework.beans.factory.support.BeanDefinitionRegistry)
 */
public final class EnableByJmsBeanFactory {
    private final ExpressionResolver expressionResolver;

    public EnableByJmsBeanFactory(final ExpressionResolver expressionResolver) {
        super();
        this.expressionResolver = expressionResolver;
    }

    public EnableByJmsConfig enableByJmsConfig(final List<Class<?>> scan, final String ttl, final String delay,
            final List<String> dispatchFns, final String requestReplyToValue,
            final DestinationType requestReplyToType) {
        return new EnableByJmsConfig(scan,
                Optional.ofNullable(expressionResolver.resolve(ttl)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                Optional.ofNullable(expressionResolver.resolve(delay)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                dispatchFns,
                Optional.ofNullable(expressionResolver.resolve(requestReplyToValue)).filter(OneUtil::hasValue).map(
                        value -> requestReplyToType == DestinationType.TOPIC ? At.toTopic(value) : At.toQueue(value))
                        .orElse(null));
    }
}
