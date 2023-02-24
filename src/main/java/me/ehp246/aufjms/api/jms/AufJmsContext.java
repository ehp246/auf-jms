package me.ehp246.aufjms.api.jms;

import java.util.Objects;

import jakarta.jms.JMSContext;
import jakarta.jms.Session;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsContext implements AutoCloseable {
    private final static AufJmsContext CONTEXT = new AufJmsContext();

    private final ThreadLocal<Session> localSession = ThreadLocal.withInitial(() -> null);
    private final ThreadLocal<JMSContext> localContext = ThreadLocal.withInitial(() -> null);

    private AufJmsContext() {
        super();
    }

    public static AufJmsContext create() {
        return new AufJmsContext();
    }

    /**
     * Sets {@linkplain JMSContext} of the current thread to the new value returning
     * the existing one.
     *
     * @param context new value. <code>null</code> accepted.
     * @return previous context. <code>null</code>, if there is none.
     */
    public AufJmsContext set(final JMSContext context) {
        localContext.set(context);
        return this;
    }

    /**
     * @return the current {@linkplain JMSContext} on the thread. <code>null</code>
     *         if there is none.
     */
    public JMSContext getJmsContext() {
        return localContext.get();
    }

    public static void set(final Session session) {
        Objects.requireNonNull(session);

        if (CONTEXT.localSession.get() != null) {
            throw new IllegalArgumentException("Context session present");
        }
        CONTEXT.localSession.set(session);
    }

    public static Session getSession() {
        return CONTEXT.localSession.get();
    }

    /**
     * Remove the session from the thread if one is present.
     *
     * @return session that is removed. Could be <code>null</code>.
     */
    public static Session clearSession() {
        final var session = CONTEXT.localSession.get();
        CONTEXT.localSession.remove();
        return session;
    }

    @Override
    public void close() throws Exception {
        final var jmsContext = localContext.get();
        localContext.set(null);

        if (jmsContext != null) {
            jmsContext.close();
        }
    }
}
