package me.ehp246.aufjms.azure.dispatch;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.qpid.jms.JmsQueue;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.jms.DestinationResolver;
import me.ehp246.aufjms.util.UtilConfig;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
@SpringBootApplication
class AppConfig {
    @Bean
    ObjectMapper objectMapper() {
        return UtilConfig.OBJECT_MAPPER;
    }

    @Bean
    public Connection connection(final ConnectionFactory connectionFactory) throws JMSException {
        return connectionFactory.createConnection();
    }

    @Bean
    DestinationResolver destinationResolver() {
        final var destination = new JmsQueue("yangle-test");
        return (c, d) -> destination;
    }

    @ByJms
    interface Case01 {
        void ping();

        void ping(Map<String, Object> map);

        void ping(Map<String, Object> map, int i);
    }
}
