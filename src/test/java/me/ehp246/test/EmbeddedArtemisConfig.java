package me.ehp246.test;

import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author Lei Yang
 *
 */
@Import({ ArtemisAutoConfiguration.class, JmsAutoConfiguration.class })
public final class EmbeddedArtemisConfig {
}
