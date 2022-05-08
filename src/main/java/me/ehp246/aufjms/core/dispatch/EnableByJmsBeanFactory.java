package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class EnableByJmsBeanFactory {
    private final PropertyResolver propertyResolver;

    public EnableByJmsBeanFactory(PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    public EnableByJmsConfig enableByJmsConfig(final List<Class<?>> scan, final String ttl,
            final List<String> dispatchFns) {
        return new EnableByJmsConfig(scan,
                Optional.ofNullable(propertyResolver.resolve(ttl)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                dispatchFns);
    }

}