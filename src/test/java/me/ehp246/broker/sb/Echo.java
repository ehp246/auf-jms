package me.ehp246.broker.sb;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @At("auf-jms.echo.inbox"), replyTo = @At("auf-jms.echo.reply"), ttl = "PT10S")
interface Echo {
    void echoInstant(Instant instant);
}
