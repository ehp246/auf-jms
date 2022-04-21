package me.ehp246.aufjms.api.dispatch;

import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class DispatchListenerTest {

    @Test
    void test() {
        final var listener = new DispatchListener() {
        };
        listener.preSend(null, null);
        listener.postSend(null, null);
        listener.onException(null, null, null);
    }

}
