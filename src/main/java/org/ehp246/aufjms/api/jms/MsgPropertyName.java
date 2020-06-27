package org.ehp246.aufjms.api.jms;

/**
 * Custom framework properties. They come after JMS headers but before
 * application properties. These values are exposed/published to the public.
 * 
 * @author Lei Yang
 *
 */
public class MsgPropertyName {
	private MsgPropertyName() {
		super();
	}

	public static final String Invoking = "AufJmsInvoking";
	public static final String ServerThrown = "ServerThrown";
	public static final String TTL = "Ttl";
	public static final String GroupId = "JMSXGroupID";
	public static final String GroupSeq = "JMSXGroupSeq";
	public static final String TraceId = "spanTraceId";
	public static final String SpanId = "spanId";
}
