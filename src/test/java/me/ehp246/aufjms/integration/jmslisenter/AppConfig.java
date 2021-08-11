package me.ehp246.aufjms.integration.jmslisenter;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.jms.DestinationProvider;
import me.ehp246.aufjms.util.TestQueueListener;
import me.ehp246.aufjms.util.UtilConfig;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
class AppConfig {
    @Bean
    ObjectMapper objectMapper() {
        return UtilConfig.OBJECT_MAPPER;
    }

    @Bean
    public Connection connection() throws JMSException {
        return UtilConfig.CONNECTION_FACTORY.createConnection();
    }

    @Bean
    DestinationProvider destinationResolver() {
        final var destination = new ActiveMQQueue(TestQueueListener.DESTINATION_NAME);
        return (c, d) -> destination;
    }

    @ByJms
    interface Case01 {
        void ping();

        void ping(Map<String, Object> map);

        void ping(Map<String, Object> map, int i);
    }
}
