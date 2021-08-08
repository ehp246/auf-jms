package me.ehp246.aufjms.api.endpoint;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface AtEndpoint {
    String getDestinationName();

    ExecutableResolver getResolver();
}
