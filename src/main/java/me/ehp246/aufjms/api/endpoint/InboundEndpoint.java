package me.ehp246.aufjms.api.endpoint;

import javax.jms.Destination;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    Destination destination();

    ExecutableResolver resolver();

    String concurrency();

    String name();
}
