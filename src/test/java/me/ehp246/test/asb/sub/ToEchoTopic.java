package me.ehp246.test.asb.sub;

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
