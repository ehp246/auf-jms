package me.ehp246.aufjms.integration.reply;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @At("echo.inbox"), replyTo = @At("echo.reply"))
interface Echo {
    void echoInstant(Instant instant);
}
