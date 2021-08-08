package me.ehp246.aufjms.core.configuration;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsProperties {
    public static final String TIMEOUT = "me.ehp246.aufjms.timeout";
    public static final String TTL = "me.ehp246.aufjms.ttl";
    public static final String POOL_SIZE = "me.ehp246.aufjms.executor.poolSize";

    public static final long TIMEOUT_DEFAULT = 30000;
    public static final int POOL_SIZE_DEFAULT = 8;

    public static final String EXECUTOR_BEAN = "c2992a60-8357-441a-8b85-ebacf8182236";

    public static final String CORRELATION_ID = "AufJms-Correlation-Id";
    public static final String MSG_TYPE = "AufJms-Msg-Type";

    private AufJmsProperties() {
        super();
    }
}
