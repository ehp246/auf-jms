package org.ehp246.aufjms.core.configuration;

/**
 * @author Lei Yang
 *
 */
public class AufJmsProperties {
	public static final String TIMEOUT = "org.ehp246.aufjms.bymsg.timeout";
	public static final String TTL = "org.ehp246.aufjms.ttl";
	public static final String POOL_SIZE = "org.ehp246.aufjms.executor.poolSize";

	public static final long TIMEOUT_DEFAULT = 30000;
	public static final int POOL_SIZE_DEFAULT = 8;

	public static final String EXECUTOR_BEAN = "c2992a60-8357-441a-8b85-ebacf8182236";

	private AufJmsProperties() {
		super();
	}

}
