package me.ehp246.aufjms.api.jms;

import javax.jms.ConnectionFactory;

/**
 * @author Lei Yang
 *
 */
public interface ConnectionFactoryProvider {
    ConnectionFactory get(String name);
}
