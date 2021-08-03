package me.ehp246.aufjms.core.byjms;

import java.util.concurrent.ExecutionException;

import javax.jms.JMSException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.jms.DispatchFnProvider;
import me.ehp246.aufjms.api.jms.MsgPropertyName;
import me.ehp246.aufjms.util.AppConfig;
import me.ehp246.aufjms.util.MockMsg;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, DefaultDispatchFnProvider.class })
class DefaultDispatchFnProviderTest {
    @Autowired
    private TestQueueListener listener;
    @Autowired
    private DispatchFnProvider dispatcherFnProvider;

    @Test
    void test() throws InterruptedException, ExecutionException, JMSException {
        final var fn = dispatcherFnProvider.get("");
        final var msg = new MockMsg();

        fn.dispatch(msg);

        final var received = listener.getReceived();

        Assertions.assertEquals(msg.type(), received.getJMSType());
        Assertions.assertEquals(msg.correlationId(), received.getJMSCorrelationID());
        Assertions.assertEquals(msg.groupId(), received.getStringProperty(MsgPropertyName.GROUP_ID));
        Assertions.assertEquals(msg.groupSeq(), received.getIntProperty(MsgPropertyName.GROUP_SEQ));
    }
}
