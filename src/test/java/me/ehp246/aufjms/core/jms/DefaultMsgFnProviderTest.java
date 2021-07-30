package me.ehp246.aufjms.core.jms;

import java.util.concurrent.ExecutionException;

import javax.jms.JMSException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.util.AppConfig;
import me.ehp246.aufjms.util.MockMsg;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, DefaultMsgFnProvider.class })
class DefaultMsgFnProviderTest {
    @Autowired
    private TestQueueListener listener;
    @Autowired
    private DefaultMsgFnProvider msgFnProvider;

    @Test
    void test() throws InterruptedException, ExecutionException, JMSException {
        final var msgFn = msgFnProvider.get();
        
        final var msg = new MockMsg();
        final var sent = msgFn.apply(msg);

        final var received = listener.getReceived();

        Assertions.assertEquals(msg.id(), received.getJMSMessageID());
    }
}
