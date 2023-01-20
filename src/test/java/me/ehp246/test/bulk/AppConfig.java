package me.ehp246.test.bulk;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@Import({ EmbeddedArtemisConfig.class })
@SpringBootApplication
@ActiveProfiles("local")
class AppConfig {

}
