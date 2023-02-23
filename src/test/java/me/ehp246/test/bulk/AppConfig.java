package me.ehp246.test.bulk;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@Import({ EmbeddedArtemisConfig.class })
class AppConfig {
}
