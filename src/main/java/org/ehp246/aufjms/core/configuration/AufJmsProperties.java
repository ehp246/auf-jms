package org.ehp246.aufjms.core.configuration;

/**
 * @author Lei Yang
 *
 */
public class AufJmsProperties {
	public static final String TIMEOUT = "org.ehp246.aufjms.bymsg.timeout";
	public static final String TTL = "org.ehp246.aufjms.ttl";

	public static final long TIMEOUT_DEFAULT = 30000;

	private AufJmsProperties() {
		super();
	}

}
