package me.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface DestinationProvider {
    Destination get(String connectionName, String destinationName);
}
