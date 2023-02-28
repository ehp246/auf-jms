package me.ehp246.aufjms.api.jms;

import java.util.HashMap;
import java.util.Map;

import jakarta.jms.JMSContext;

/**
 * @author Lei Yang
 */
public final class JmsDispatchContext {
    private static final ThreadLocal<JMSContext> localContext = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Map<String, Object>> localPropertyMap = ThreadLocal.withInitial(HashMap::new);

    private JmsDispatchContext() {
        super();
    }

    public static AutoCloseable setProperties(final Map<String, Object> map) {
        localPropertyMap.set(map);

        return JmsDispatchContext.closeable();
    }

    public static Map<String, Object> properties() {
        return localPropertyMap.get();
    }

    public static AutoCloseable setJmsContext(final JMSContext jmsContext) {
        localContext.set(jmsContext);

        return JmsDispatchContext.closeable();
    }

    /**
     * @return the current {@linkplain JMSContext} on the thread. <code>null</code>
     *         if there is none.
     */
    public static JMSContext jmsContext() {
        return localContext.get();
    }

    /**
     * Clears all thread-specific data.
     */
    public static void remove() {
        localContext.remove();
        localPropertyMap.remove();
    }

    public static void close() {
        final var jmsContext = localContext.get();

        remove();

        if (jmsContext != null) {
            jmsContext.close();
        }
    }

    public static AutoCloseable closeable() {
        return JmsDispatchContext::close;
    }
}
