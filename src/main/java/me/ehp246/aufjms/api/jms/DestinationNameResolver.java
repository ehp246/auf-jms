package me.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface DestinationNameResolver {
    Destination resolve(String connectionName, String destinationName);
}
