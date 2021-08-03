package me.ehp246.aufjms.api.jms;

import javax.jms.Connection;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface NamedConnectionProvider {
    Connection get(String name);
}
