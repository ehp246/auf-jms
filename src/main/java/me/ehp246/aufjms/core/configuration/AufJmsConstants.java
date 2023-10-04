package me.ehp246.aufjms.core.configuration;

import java.util.Set;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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

    public static final String LOG4J_CONTEXT_HEADER_PREFIX = "AufJmsLog4jContext";

    public static final Set<String> RESERVED_PROPERTIES = Set.of(JmsNames.GROUP_ID, JmsNames.GROUP_SEQ);

    /**
     * Spring configuration property.
     */
    public static final String DISPATCH_LOGTER = "me.ehp246.aufjms.dispatchlogger.enabled";

    /**
     * Log4J
     */
    public final static Marker HEADERS = MarkerManager.getMarker("HEADERS");
    public final static Marker PROPERTIES = MarkerManager.getMarker("PROPERTIES");
    public final static Marker BODY = MarkerManager.getMarker("BODY");
    public final static Marker EXCEPTION = MarkerManager.getMarker("EXCEPTION");

    private AufJmsConstants() {
        super();
    }
}
