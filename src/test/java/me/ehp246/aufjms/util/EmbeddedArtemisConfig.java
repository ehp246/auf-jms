package me.ehp246.aufjms.util;

import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lei Yang
 *
 */
@Import({ ArtemisAutoConfiguration.class, JmsAutoConfiguration.class })
public class EmbeddedArtemisConfig {

    @Bean
    ObjectMapper objectMapper() {
        return TestUtil.OBJECT_MAPPER;
    }
}
