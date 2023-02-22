package me.ehp246.aufjms.core.dispatch;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public final class DefaultDispatchFnProvider implements JmsDispatchFnProvider, AutoCloseable {
    private final static Logger LOGGER = LogManager.getLogger(DefaultDispatchFnProvider.class);

    private final ConnectionFactoryProvider cfProvider;
    private final ToJson toJson;
    private final List<DispatchListener> dispatchListeners;
    private final Set<AutoCloseable> closeable = ConcurrentHashMap.newKeySet();

    public DefaultDispatchFnProvider(final ConnectionFactoryProvider cfProvider, final ToJson jsonFn,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.cfProvider = Objects.requireNonNull(cfProvider);
        this.toJson = Objects.requireNonNull(jsonFn);
        this.dispatchListeners = dispatchListeners;
    }

    @Override
    public JmsDispatchFn get(final String connectionFactoryName) {
        final var dispatchFn = new DefaultDispatchFn(toJson, cfProvider.get(connectionFactoryName), this.dispatchListeners);
        this.closeable.add(dispatchFn);

        return dispatchFn;
    }

    @Override
    public void close() {
        closeable.stream().forEach(t -> {
            try {
                t.close();
            } catch (final Exception e) {
                LOGGER.atError().withThrowable(e).log("Close failed, ignoring: {}", e::getMessage);
            }
        });
        closeable.clear();
    }
}
