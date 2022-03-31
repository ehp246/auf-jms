package me.ehp246.broker.sb.sub;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("auf-jms.echo.event"))
interface ToEchoTopic {
    void echo(int i);
}
