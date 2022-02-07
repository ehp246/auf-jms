package me.ehp246.broker.sb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.util.SimpleServiceBusConfig;
import me.ehp246.broker.sb.dlq.dlq.LetterCollection;
import me.ehp246.broker.sb.dlq.trigger.OnThrowIt;
import me.ehp246.broker.sb.replyto.inbox.OnInboxEchoInstant;
import me.ehp246.broker.sb.replyto.reply.OnReplyEchoInstant;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({ @Inbound(value = @At("auf-jms.echo.inbox"), scan = OnInboxEchoInstant.class),
        @Inbound(value = @At("auf-jms.echo.reply"), scan = OnReplyEchoInstant.class),
        @Inbound(value = @At("auf-jms.dlq"), scan = OnThrowIt.class),
        @Inbound(value = @At("auf-jms.dlq/$deadletterqueue"), scan = LetterCollection.class, autoStartup = "true") })
@Import({ SimpleServiceBusConfig.class, OnReplyEchoInstant.class, LetterCollection.class })
@SpringBootApplication
class AppConfig {

}
