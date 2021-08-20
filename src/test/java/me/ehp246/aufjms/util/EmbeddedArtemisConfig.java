package me.ehp246.aufjms.util;

import javax.jms.ConnectionFactory;

import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.jms.ContextProvider;

/**
 * @author Lei Yang
 *
 */
@Import(ArtemisAutoConfiguration.class)
public class EmbeddedArtemisConfig {
    @Bean
    public ContextProvider contextProvider(final ConnectionFactory connectionFactory) {
        final var context = connectionFactory.createContext();
        return name -> context;
    }

    @Bean
    ObjectMapper objectMapper() {
        return UtilConfig.OBJECT_MAPPER;
    }
}
