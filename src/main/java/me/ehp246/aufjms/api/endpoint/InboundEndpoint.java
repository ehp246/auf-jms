package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.AtDestination;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    AtDestination at();

    ExecutableResolver resolver();

    String concurrency();

    String name();
}
