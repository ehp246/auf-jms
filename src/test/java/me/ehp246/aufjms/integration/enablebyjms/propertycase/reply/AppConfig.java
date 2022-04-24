package me.ehp246.aufjms.integration.enablebyjms.propertycase.reply;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @ByJms(value = @To("queue"), replyTo = @To("${holder}"))
    interface Case01 {
    }
}
