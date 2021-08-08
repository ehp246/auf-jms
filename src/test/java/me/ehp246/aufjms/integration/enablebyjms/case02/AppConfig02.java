package me.ehp246.aufjms.integration.enablebyjms.case02;

import org.apache.activemq.command.ActiveMQTempQueue;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.jms.DestinationNameResolver;
import me.ehp246.aufjms.integration.enablebyjms.case01.ScanCase01;
import me.ehp246.aufjms.util.UtilConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(scan = ScanCase01.class)
public class AppConfig02 {
    @Bean
    ObjectMapper objectMapper() {
        return UtilConfig.OBJECT_MAPPER;
    }

    @Bean
    DestinationNameResolver destinationResolver() {
        final var dest = new ActiveMQTempQueue();
        return (c, d) -> dest;
    }
}
