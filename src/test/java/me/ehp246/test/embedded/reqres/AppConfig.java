package me.ehp246.test.embedded.reqres;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableByJms.ReturnsAt;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(returnsAt = @ReturnsAt("response.topic"))
@EnableForJms(@Inbound(@From("inbox")))
@Import({ EmbeddedArtemisConfig.class })
class AppConfig {
}
