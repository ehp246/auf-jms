package me.ehp246.aufjms.core.dispatch;

import java.util.List;
import java.util.Objects;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.ToJson;

/**
 * @author Lei Yang
 * @since 1.0
 * @see EnableByJmsRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata,
 *      org.springframework.beans.factory.support.BeanDefinitionRegistry)
 */
public final class DefaultDispatchFnProvider implements JmsDispatchFnProvider {
    private final ConnectionFactoryProvider cfProvider;
    private final ToJson toJson;
    private final List<DispatchListener> dispatchListeners;

    public DefaultDispatchFnProvider(final ConnectionFactoryProvider cfProvider, final ToJson toJson,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.cfProvider = Objects.requireNonNull(cfProvider);
        this.toJson = Objects.requireNonNull(toJson);
        this.dispatchListeners = dispatchListeners;
    }

    @Override
    public JmsDispatchFn get(final String connectionFactoryName) {
        return new DefaultDispatchFn(cfProvider.get(connectionFactoryName), toJson, this.dispatchListeners);
    }
}
