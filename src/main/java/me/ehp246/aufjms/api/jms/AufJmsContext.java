package me.ehp246.aufjms.api.jms;

import java.util.Objects;

import javax.jms.Session;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsContext {
    private final static AufJmsContext CONTEXT = new AufJmsContext();

    private final ThreadLocal<Session> threadLocalSession = ThreadLocal.withInitial(() -> null);

    private AufJmsContext() {
        super();
    }

    public static void set(Session session) {
        Objects.requireNonNull(session);

        if (CONTEXT.threadLocalSession.get() != null) {
            throw new RuntimeException("Context session present");
        }
        CONTEXT.threadLocalSession.set(session);
    }

    public static Session getSession() {
        return CONTEXT.threadLocalSession.get();
    }

    /**
     * Remove the session from the thread if one is present.
     * 
     * @return session that is removed. Could be <code>null</code>.
     */
    public static Session clearSession() {
        final var session = CONTEXT.threadLocalSession.get();
        CONTEXT.threadLocalSession.remove();
        return session;
    }
}
