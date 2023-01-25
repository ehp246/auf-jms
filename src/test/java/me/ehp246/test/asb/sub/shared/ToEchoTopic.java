package me.ehp246.test.asb.sub;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To(value = "auf-jms.echo.event", type = DestinationType.TOPIC))
interface ToEchoTopic {
    void echo(int i);
}
