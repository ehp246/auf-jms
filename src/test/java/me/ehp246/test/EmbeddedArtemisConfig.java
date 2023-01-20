package me.ehp246.test;

import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.spi.JacksonConfig;

/**
 * @author Lei Yang
 *
 */
@Import({ ArtemisAutoConfiguration.class, JmsAutoConfiguration.class, JacksonConfig.class })
public final class EmbeddedArtemisConfig {
}
