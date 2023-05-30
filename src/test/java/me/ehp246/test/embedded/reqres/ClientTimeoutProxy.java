package me.ehp246.test.embedded.reqres;

import java.util.concurrent.TimeoutException;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("inbox"), replyTimeout = "${reply.timeout}")
interface ClientTimeoutProxy {
    int nonInc(int i);

    int incThrowing(int i) throws TimeoutException;
}
