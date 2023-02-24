package me.ehp246.aufjms.api.jms;

import jakarta.jms.JMSContext;

/**
 * @author Lei Yang
 */
public final class JmsDispatchContext {
    private static final ThreadLocal<JMSContext> localContext = ThreadLocal.withInitial(() -> null);

    private JmsDispatchContext() {
        super();
    }

    public static AutoCloseable set(final JMSContext jmsContext) {
        localContext.set(jmsContext);

        return JmsDispatchContext.closeable();
    }

    /**
     * @return the current {@linkplain JMSContext} on the thread. <code>null</code>
     *         if there is none.
     */
    public static JMSContext getJmsContext() {
        return localContext.get();
    }

    /**
     * Clears all thread-specific data.
     */
    public static void remove() {
        localContext.remove();
    }

    public static AutoCloseable closeable() {
        return () -> {
            final var jmsContext = localContext.get();

            JmsDispatchContext.remove();

            if (jmsContext != null) {
                jmsContext.close();
            }
        };
    }
}
