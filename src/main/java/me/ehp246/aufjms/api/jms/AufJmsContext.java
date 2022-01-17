package me.ehp246.aufjms.api.jms;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author Lei Yang
 *
 */
public final class AufJmsContext {
    private final static AufJmsContext CONTEXT = new AufJmsContext();

    private final ThreadLocal<Map<Class<?>, Object>> threadLocalMap = ThreadLocal.withInitial(HashMap::new);

    private AufJmsContext() {
        super();
    }

    public static Session set(Session session) {
        if (session == null) {
            return AufJmsContext.clearSession();
        }
        return (Session) CONTEXT.threadLocalMap.get().put(Session.class, session);
    }

    public static Session getSession() {
        return (Session) CONTEXT.threadLocalMap.get().get(Session.class);
    }

    public static Session clearSession() {
        return (Session) CONTEXT.threadLocalMap.get().remove(Session.class);
    }

    public static void closeSession() {
        Optional.ofNullable(AufJmsContext.clearSession()).ifPresent(session -> {
            try {
                session.close();
            } catch (JMSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}
