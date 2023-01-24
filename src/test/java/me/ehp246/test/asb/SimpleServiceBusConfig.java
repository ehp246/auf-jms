package me.ehp246.test.asb;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.SingleConnectionFactory;

import jakarta.jms.ConnectionFactory;

/**
 * @author Lei Yang
 *
 */
public class SimpleServiceBusConfig {
    @Value("${test.servicebus.url}")
    private String url;
    @Value("${test.servicebus.username}")
    private String username;
    @Value("${test.servicebus.password}")
    private String password;

    @Bean
    public ConnectionFactory jmsConnectionFactoryQpid() {
        return new SingleConnectionFactory(new JmsConnectionFactory(username, password, url));
    }
}
