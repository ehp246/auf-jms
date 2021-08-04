package me.ehp246.aufjms.integration.enablebyjms.case01;

import org.apache.activemq.command.ActiveMQTempQueue;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.jms.DestinationResolver;
import me.ehp246.aufjms.util.UtilConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
public class AppConfig01 {
    @Bean
    ObjectMapper objectMapper() {
        return UtilConfig.OBJECT_MAPPER;
    }

    @Bean
    DestinationResolver destinationResolver() {
        final var dest = new ActiveMQTempQueue();
        return (c, d) -> dest;
    }
}
