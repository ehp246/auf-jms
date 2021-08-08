package me.ehp246.aufjms.core.configuration;

import javax.jms.Connection;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.jms.ConnectionNameResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsConfiguration {
    @Bean
    public ConnectionNameResolver connectionMap(final ListableBeanFactory beanFactory) {
        return name -> OneUtil.hasValue(name) ? beanFactory.getBean(name, Connection.class)
                : beanFactory.getBean(Connection.class);
    }
}
