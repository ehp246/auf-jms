package me.ehp246.aufjms.integration.reply;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.integration.reply.inbox.OnInboxEchoInstant;
import me.ehp246.aufjms.integration.reply.reply.OnReplyEchoInstant;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({ @Inbound(value = @At("echo.inbox"), scan = OnInboxEchoInstant.class),
        @Inbound(value = @At("echo.reply"), scan = OnReplyEchoInstant.class) })
@Import({ EmbeddedArtemisConfig.class, OnReplyEchoInstant.class })
class AppConfig {
}
