package me.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface DestinationResolver {
    Destination resolve(String connectionName, String destinationName);
}
