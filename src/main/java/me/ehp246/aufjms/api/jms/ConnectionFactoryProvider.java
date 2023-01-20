package me.ehp246.aufjms.api.jms;

import jakarta.jms.ConnectionFactory;
import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;

/**
 * The abstraction that resolves a name to a {@linkplain ConnectionFactory}
 * <p>
 * Available as a Spring bean.
 *
 * @author Lei Yang
 * @see AufJmsConfiguration#connectionFactoryProvider(org.springframework.beans.factory.BeanFactory)
 */
public interface ConnectionFactoryProvider {
    ConnectionFactory get(String name);
}
