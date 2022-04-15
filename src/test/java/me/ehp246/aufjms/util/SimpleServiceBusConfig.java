package me.ehp246.aufjms.util;

import javax.jms.ConnectionFactory;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.SingleConnectionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lei Yang
 *
 */
public class SimpleServiceBusConfig {
    @Value("${aufjms.servicebus.url}")
    private String url;
    @Value("${aufjms.servicebus.username}")
    private String username;
    @Value("${aufjms.servicebus.password}")
    private String password;

    @Bean
    public ConnectionFactory jmsConnectionFactoryQpid() {
        return new SingleConnectionFactory(new JmsConnectionFactory(username, password, url));
    }

    @Bean
    ObjectMapper objectMapper() {
        return TestUtil.OBJECT_MAPPER;
    }
}
