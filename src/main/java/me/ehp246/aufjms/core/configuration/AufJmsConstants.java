package me.ehp246.aufjms.core.configuration;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsConstants {
    public static final String TIMEOUT = "me.ehp246.aufjms.timeout";
    public static final String TTL = "me.ehp246.aufjms.ttl";
    public static final String POOL_SIZE = "me.ehp246.aufjms.executor.pool-size";
    public static final String DISPATCH_LOGTER = "me.ehp246.aufjms.dispatch-logger.enabled";

    public static final long TIMEOUT_DEFAULT = 30000;

    private AufJmsConstants() {
        super();
    }
}
