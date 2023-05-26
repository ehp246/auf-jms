package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig.ReplyAt;
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
            final List<String> dispatchFns, final String replyAtValue, final DestinationType replyAtType) {
        return new EnableByJmsConfig(scan,
                Optional.ofNullable(propertyResolver.resolve(ttl)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                Optional.ofNullable(propertyResolver.resolve(delay)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                dispatchFns, new ReplyAt(replyAtValue, replyAtType));
    }

    public ReturningDispatchRepo returningDispatchRepo() {
        return new ReturningDispatchRepo();
    }
}
