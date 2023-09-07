package me.ehp246.aufjms.core.configuration;

import java.util.Set;

import me.ehp246.aufjms.api.jms.JmsNames;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsConstants {
    public static final String REQUEST_TIMEOUT = "me.ehp246.aufjms.request.timeout";
    public static final String TTL = "me.ehp246.aufjms.ttl";
    public static final String POOL_SIZE = "me.ehp246.aufjms.executor.pool-size";
    public static final String AUF_JMS_OBJECT_MAPPER = "aufJmsObjectMapper";

    public static final String LOG4J_THREAD_CONTEXT_HEADER_PREFIX = "AufJmsLog4jThreadContext";

    public static final Set<String> RESERVED_PROPERTIES = Set.of(JmsNames.GROUP_ID, JmsNames.GROUP_SEQ);

    /**
     * Spring configuration property.
     */
    public static final String DISPATCH_LOGTER = "me.ehp246.aufjms.dispatchlogger.enabled";

    private AufJmsConstants() {
        super();
    }
}
