package me.ehp246.aufjms.api.jms;

/**
 * Custom framework properties. They come after JMS headers but before
 * application properties. These values are exposed/published to the public.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final class PropertyName {
    private PropertyName() {
        super();
    }

    public static final String INVOKING = "AufJmsInvoking";
    public static final String ServerThrown = "AufJmsServerThrown";
    public static final String TTL = "AufJmsTtl";
    public static final String GROUP_ID = "JMSXGroupID";
    public static final String GROUP_SEQ = "JMSXGroupSeq";
    public static final String TraceId = "spanTraceId";
    public static final String SpanId = "spanId";
}