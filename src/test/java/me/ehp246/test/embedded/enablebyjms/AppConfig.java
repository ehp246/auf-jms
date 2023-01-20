package me.ehp246.test.embedded.enablebyjms;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.embedded.enablebyjms.case01.Case01;
import me.ehp246.test.embedded.enablebyjms.case02.Case02;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(scan = { Case01.class, Case02.class })
@Import({ EmbeddedArtemisConfig.class })
class AppConfig {
}
