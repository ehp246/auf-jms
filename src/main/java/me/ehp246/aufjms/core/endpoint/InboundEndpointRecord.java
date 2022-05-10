package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint.From.Sub;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.MsgInvocableFactory;
import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 *
 */
record InboundEndpointRecord(InboundEndpoint.From from, MsgInvocableFactory invocableFactory, int concurrency, String name,
        boolean autoStartup, String connectionFactory, InvocationListener invocationListener)
        implements InboundEndpoint {

    record From(At on, String selector, Sub sub) implements InboundEndpoint.From {
    }
    
    record Sub(String name, boolean shared, boolean durable) implements InboundEndpoint.From.Sub {
    }
}
