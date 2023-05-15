package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufjms.api.annotation.EnableByJms.ReturnsAt;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.dispatch.ReturningDispatcheRepo.ReturningDispatch;
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
            final List<String> dispatchFns, final ReturnsAt returnsAt) {
        return new EnableByJmsConfig(scan,
                Optional.ofNullable(propertyResolver.resolve(ttl)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                Optional.ofNullable(propertyResolver.resolve(delay)).filter(OneUtil::hasValue).map(Duration::parse)
                        .orElse(null),
                dispatchFns, returnsAt);
    }

    public ReturningDispatcheRepo returningDispatcheRepo() {
        final var map = new ConcurrentHashMap<String, ReturningDispatch>();

        return new ReturningDispatcheRepo() {

            @Override
            public ReturningDispatch take(final String correlationId) {
                return map.remove(correlationId);
            }

            @Override
            public ReturningDispatch add(final String correlationId, final RemoteReturnBinder binder) {
                return map.putIfAbsent(correlationId, new ReturningDispatch(binder, new CompletableFuture<Object>()));
            }
        };
    }
}
