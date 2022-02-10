package me.ehp246.aufjms.integration.reply;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("echo.inbox"), replyTo = @To("echo.reply"))
interface Echo {
    void echoInstant(Instant instant);
}
