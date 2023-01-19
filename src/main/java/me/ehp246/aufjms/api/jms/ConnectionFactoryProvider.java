package me.ehp246.aufjms.api.jms;

import jakarta.jms.ConnectionFactory;

/**
 * @author Lei Yang
 *
 */
public interface ConnectionFactoryProvider {
    ConnectionFactory get(String name);
}
