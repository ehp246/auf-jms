package me.ehp246.aufjms.integration.enablebyjms;

import org.apache.activemq.command.ActiveMQTempQueue;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.jms.DestinationProvider;
import me.ehp246.aufjms.integration.enablebyjms.case01.Case01;
import me.ehp246.aufjms.integration.enablebyjms.case02.Case02;
import me.ehp246.aufjms.util.UtilConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(scan = { Case01.class, Case02.class })
class AppConfig {
    @Bean
    ObjectMapper objectMapper() {
        return UtilConfig.OBJECT_MAPPER;
    }

    @Bean
    DestinationProvider destinationResolver() {
        final var dest = new ActiveMQTempQueue();
        return (c, d) -> dest;
    }
}
