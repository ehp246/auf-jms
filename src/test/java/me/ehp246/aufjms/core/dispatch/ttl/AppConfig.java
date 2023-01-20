package me.ehp246.aufjms.core.dispatch.ttl;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.aufjms.api.annotation.EnableByJms;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @ByJms(value = @To("queue"), ttl = "${holder}")
    interface Case01 {
    }
}
