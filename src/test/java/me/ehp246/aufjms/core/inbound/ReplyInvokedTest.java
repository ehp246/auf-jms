package me.ehp246.aufjms.core.inbound;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.Topic;
import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.inbound.ReplyInvoked;
import me.ehp246.aufjms.core.util.TextJmsMsg;
import me.ehp246.test.mock.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class ReplyInvokedTest {
    private final JmsMsg msg = TextJmsMsg.from(new MockTextMessage(UUID.randomUUID().toString()));
    private final BoundInvocable bound = Mockito.mock(BoundInvocable.class);
    private final Completed completed = Mockito.mock(Completed.class);
    private final Object expectedBody = new Object();

    @BeforeEach
    void setup() {
        Mockito.when(completed.returned()).thenReturn(expectedBody);
        Mockito.when(completed.bound()).thenReturn(bound);
        Mockito.when(bound.msg()).thenReturn(msg);
    }

    @Test
    void reply_01() throws JMSException {
        final var dispatchRef = new JmsDispatch[1];

        new ReplyInvoked(dispatch -> {
            dispatchRef[0] = dispatch;
            return null;
        }).onCompleted(completed);

        // Supported reply message: To, type, returned, correlation
        Assertions.assertEquals(true, dispatchRef[0].to() instanceof AtQueue);
        Assertions.assertEquals(((Queue) msg.replyTo()).getQueueName(), dispatchRef[0].to().name());
        Assertions.assertEquals(msg.type(), dispatchRef[0].type());
        Assertions.assertEquals(msg.correlationId(), dispatchRef[0].correlationId());
        Assertions.assertEquals(expectedBody, dispatchRef[0].body());
    }

    @Test
    void reply_02() throws JMSException {
        final var dispatchRef = new JmsDispatch[1];
        final var mock = Mockito.mock(Topic.class);
        Mockito.when(mock.getTopicName()).thenReturn("t1");

        Mockito.when(bound.msg()).thenReturn(TextJmsMsg.from(new MockTextMessage() {

            @Override
            public String getJMSCorrelationID() throws JMSException {
                return null;
            }

            @Override
            public Destination getJMSReplyTo() throws JMSException {
                return mock;
            }
        }));
        Mockito.when(completed.returned()).thenReturn(null);

        new ReplyInvoked(dispatch -> {
            dispatchRef[0] = dispatch;
            return null;
        }).onCompleted(completed);

        Assertions.assertEquals(true, dispatchRef[0].to() instanceof AtTopic);
        Assertions.assertEquals("t1", dispatchRef[0].to().name());
        Assertions.assertEquals(null, dispatchRef[0].type());
        Assertions.assertEquals(null, dispatchRef[0].correlationId());
        Assertions.assertEquals(null, dispatchRef[0].body());
    }

    @Test
    void reply_03() throws JMSException {
        final var dispatchRef = new JmsDispatch[1];

        Mockito.when(bound.msg()).thenReturn(TextJmsMsg.from(new MockTextMessage() {

            @Override
            public String getJMSCorrelationID() throws JMSException {
                return null;
            }

            @Override
            public Destination getJMSReplyTo() throws JMSException {
                return null;
            }
        }));

        new ReplyInvoked(dispatch -> {
            dispatchRef[0] = dispatch;
            return null;
        }).onCompleted(completed);

        // Supported reply message: To, type, returned, correlation
        Assertions.assertEquals(null, dispatchRef[0], "should have no reply");
    }
}
