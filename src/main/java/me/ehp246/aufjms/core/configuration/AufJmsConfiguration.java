package me.ehp246.aufjms.core.configuration;

import javax.jms.Connection;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.jms.NamedConnectionProvider;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsConfiguration {
    @Bean
    public NamedConnectionProvider connectionMap(final ListableBeanFactory beanFactory) {
        return name -> beanFactory.getBean(Connection.class);
    }
}
