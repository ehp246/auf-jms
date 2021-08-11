package me.ehp246.aufjms.api.jms;

import javax.jms.Connection;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ConnectionProvider {
    Connection get(String name);
}
