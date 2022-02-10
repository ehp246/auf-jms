package me.ehp246.broker.sb;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("auf-jms.echo.inbox"), replyTo = @To("auf-jms.echo.reply"), ttl = "PT10S")
interface Echo {
    void echoInstant(Instant instant);
}
