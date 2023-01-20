package me.ehp246.test.embedded.reply.inbox;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJmsType("EchoInstant")
public class OnInboxEchoInstant {
    @Invoking
    public Instant echo(final Instant instant) {
        return instant;
    }
}
