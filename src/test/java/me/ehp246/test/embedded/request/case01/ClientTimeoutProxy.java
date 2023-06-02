package me.ehp246.test.embedded.request.case01;

import java.util.concurrent.TimeoutException;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("inbox"), requestTimeout = "${reply.timeout}")
interface ClientTimeoutProxy {
    int nonInc(int i);

    int incThrowing(int i) throws TimeoutException;
}
