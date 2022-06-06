package me.ehp246.aufjms.util;

import javax.jms.ConnectionFactory;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.connection.SingleConnectionFactory;

import me.ehp246.aufjms.api.spi.JacksonConfig;

/**
 * @author Lei Yang
 *
 */
@Import(JacksonConfig.class)
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
}
