package me.ehp246.aufjms.util;

import javax.jms.Connection;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.jms.ConnectionProvider;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public class MockJmsConfig {
    @Bean
    public ConnectionProvider connectionMap(final ListableBeanFactory beanFactory) {
        return name -> OneUtil.hasValue(name) ? beanFactory.getBean(name, Connection.class)
                : beanFactory.getBean(Connection.class);
    }

}
