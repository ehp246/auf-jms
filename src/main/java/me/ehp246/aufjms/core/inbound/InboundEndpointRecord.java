package me.ehp246.aufjms.core.inbound;

import org.springframework.util.ErrorHandler;

import jakarta.jms.ExceptionListener;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.InboundEndpoint.From.Sub;
import me.ehp246.aufjms.api.inbound.InvocableTypeRegistry;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.MsgConsumer;
import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 *
 */
record InboundEndpointRecord(InboundEndpoint.From from, InvocableTypeRegistry typeRegistry, int concurrency,
        String name, boolean autoStartup, String connectionFactory, InvocationListener invocationListener,
        MsgConsumer defaultConsumer, int sessionMode, ErrorHandler errorHandler, ExceptionListener exceptionListener)
        implements InboundEndpoint {

    record From(At on, String selector, Sub sub) implements InboundEndpoint.From {
    }

    record Sub(String name, boolean shared, boolean durable) implements InboundEndpoint.From.Sub {
    }
}
