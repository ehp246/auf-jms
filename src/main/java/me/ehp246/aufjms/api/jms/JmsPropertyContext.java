package me.ehp246.aufjms.api.jms;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class JmsPropertyContext {
    private final static String TYPE = "ae5dbae8-13b5-40f5-a852-065b67755a29";
    private final static String CORRELATIONID = "36da23de-e9a2-4ae7-927b-03a852bc312f";
    
    private final static JmsPropertyContext CONTEXT = new JmsPropertyContext();

    private final ThreadLocal<Map<String, String>> jmsProperties = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<String, String>> appProperties = ThreadLocal.withInitial(HashMap::new);

    private JmsPropertyContext() {
        super();
    }

    private static Map<String, String> jmsMap() {
        return CONTEXT.jmsProperties.get();
    }

    private static Map<String, String> appMap() {
        return CONTEXT.appProperties.get();
    }

    public static void clearAll() {
        jmsMap().clear();
        appMap().clear();
    }

    public static void setJmsType(final String type) {
        jmsMap().put(TYPE, type);
    }

    public static String getJmsType() {
        return jmsMap().get(TYPE);
    }

    public static void setJmsCorrelationId(final String id) {
        jmsMap().put(CORRELATIONID, id);
    }

    public static String getJmsCorrelationId() {
        return jmsMap().get(CORRELATIONID);
    }

    public static void clearJmsProperties() {
        jmsMap().clear();
    }

    public static void setAppProperty(final String name, final String value) {
        appMap().put(name, value);
    }

    public static String getAppProperty(final String name) {
        return appMap().get(name);
    }

    public static void removeAppProperty(final String name) {
        appMap().remove(name);
    }

    public static void clearAppProperties() {
        appMap().clear();
    }
}
