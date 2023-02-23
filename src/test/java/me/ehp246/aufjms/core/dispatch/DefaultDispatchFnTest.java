package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.exception.JmsDispatchFailedException;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.ToJson;
import me.ehp246.test.mock.MockDispatch;

/**
 * @author Lei Yang
 *
 */
class DefaultDispatchFnTest {
    private final static ToJson toNullJson = (value, info) -> null;

    private final DispatchListener.OnDispatch onDispatch = Mockito.mock(DispatchListener.OnDispatch.class);
    private final DispatchListener.PreSend preSend = Mockito.mock(DispatchListener.PreSend.class);
    private final DispatchListener.PostSend postSend = Mockito.mock(DispatchListener.PostSend.class);
    private final DispatchListener.OnException onException = Mockito.mock(DispatchListener.OnException.class);

    private final List<DispatchListener> listeners = List.of(onDispatch, preSend, postSend, onException, onDispatch,
            preSend, postSend, onException);

    private final ArgumentMatcher<JmsMsg> matchNullMessage = msg -> msg == null;

    private MockProducer newProducer() {
        final var cf = Mockito.mock(ConnectionFactory.class);
        final var jmsContext = Mockito.mock(JMSContext.class);
        final var producer = Mockito.mock(JMSProducer.class);
        final var message = Mockito.mock(TextMessage.class);
        final var queue = Mockito.mock(Queue.class);

        final ArgumentMatcher<JmsMsg> matchMessage = msg -> msg.message() == message;

        Mockito.when(cf.createContext()).thenReturn(jmsContext);

        Mockito.when(jmsContext.createProducer()).thenReturn(producer);
        Mockito.when(jmsContext.createTextMessage()).thenReturn(message);
        Mockito.when(jmsContext.createQueue(Mockito.anyString())).thenReturn(queue);

        Mockito.when(producer.setDeliveryDelay(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.setTimeToLive(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.send(ArgumentMatchers.any(), ArgumentMatchers.<TextMessage>any())).thenReturn(producer);

        return new MockProducer(cf, jmsContext, producer, message, queue, matchMessage);
    }

    @Test
    void ex_01() throws JMSException {
        final var expected = new JMSRuntimeException("");
        final var cf = Mockito.mock(ConnectionFactory.class);

        Mockito.doThrow(expected).when(cf).createContext();

        final var thrown = Assertions.assertThrows(JmsDispatchFailedException.class,
                () -> new DefaultDispatchFn(cf, toNullJson, listeners).send(new MockDispatch()));

        Assertions.assertEquals(expected, thrown.getCause());
    }

    @Test
    void cleanup_01() {
        final var cf = Mockito.mock(ConnectionFactory.class);
        final var jmsContext = Mockito.mock(JMSContext.class);
        final var producer = Mockito.mock(JMSProducer.class);
        final var message = Mockito.mock(TextMessage.class);
        final var queue = Mockito.mock(Queue.class);

        Mockito.when(cf.createContext()).thenReturn(jmsContext);

        Mockito.when(jmsContext.createProducer()).thenReturn(producer);
        Mockito.when(jmsContext.createTextMessage()).thenReturn(message);
        Mockito.when(jmsContext.createQueue(Mockito.anyString())).thenReturn(queue);

        Mockito.when(producer.setDeliveryDelay(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.setTimeToLive(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.send(ArgumentMatchers.any(), ArgumentMatchers.<TextMessage>any())).thenReturn(producer);

        final var jmsMsg = new DefaultDispatchFn(cf, toNullJson, null).send(new MockDispatch());

        Mockito.verify(producer).send(ArgumentMatchers.eq(queue), ArgumentMatchers.eq(jmsMsg.message()));
        // Should clean up everything.
        Mockito.verify(jmsContext).close();
    }

    @Test
    void cleanup_02() {
        final var jmsException = new JMSRuntimeException("");

        final var cf = Mockito.mock(ConnectionFactory.class);
        final var jmsContext = Mockito.mock(JMSContext.class);
        final var producer = Mockito.mock(JMSProducer.class);
        final var message = Mockito.mock(TextMessage.class);
        final var queue = Mockito.mock(Queue.class);

        Mockito.when(cf.createContext()).thenReturn(jmsContext);

        Mockito.when(jmsContext.createProducer()).thenReturn(producer);
        Mockito.when(jmsContext.createTextMessage()).thenReturn(message);
        Mockito.when(jmsContext.createQueue(Mockito.anyString())).thenReturn(queue);

        Mockito.when(producer.setDeliveryDelay(ArgumentMatchers.anyLong())).thenReturn(producer);

        Mockito.when(producer.send(ArgumentMatchers.any(), ArgumentMatchers.<TextMessage>any())).thenReturn(producer);

        Mockito.doThrow(jmsException).when(producer).setTimeToLive(ArgumentMatchers.anyLong());

        final var actual = Assertions.assertThrows(JmsDispatchFailedException.class,
                () -> new DefaultDispatchFn(cf, toNullJson, null).send(new MockDispatch()));

        Assertions.assertEquals(jmsException, actual.getCause());

        // Should clean up everything.
        Mockito.verify(jmsContext).close();
    }

    @Test
    void ondispatch_ex_01() throws JMSException {
        final var expected = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final DispatchListener.OnDispatch listener = d -> {
            throw expected;
        };

        final var cf = Mockito.mock(ConnectionFactory.class);
        final var actual = Assertions
                .assertThrows(JmsDispatchFailedException.class,
                        () -> new DefaultDispatchFn(cf, toNullJson, List.of(listener, onException)).send(dispatch))
                .getCause();

        Mockito.verify(onException).onException(Mockito.eq(dispatch), Mockito.argThat(matchNullMessage),
                Mockito.eq(expected));

        // Nothing should be created.
        Mockito.verify(cf, times(0)).createContext();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void presend_ex_02() throws JMSException {
        final var expected = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final DispatchListener.PreSend listener = (d, m) -> {
            throw expected;
        };

        final var mockProducer = this.newProducer();
        final var fn = new DefaultDispatchFn(mockProducer.conFactory(), toNullJson, List.of(listener, onException));

        final var actual = Assertions.assertThrows(JmsDispatchFailedException.class, () -> fn.send(dispatch))
                .getCause();

        Mockito.verify(onException).onException(Mockito.eq(dispatch), Mockito.argThat(mockProducer.matchMessage()),
                Mockito.eq(expected));
        Mockito.verify(mockProducer.jmsContext()).close();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void postsend_ex_01() throws JMSException {
        final var expected = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final DispatchListener.PostSend postListener = (d, m) -> {
            throw expected;
        };
        final DispatchListener.PostSend PostListener2 = Mockito.mock(DispatchListener.PostSend.class);

        final var mockProducer = newProducer();

        final var fn = new DefaultDispatchFn(mockProducer.conFactory, toNullJson,
                List.of(postListener, PostListener2, onException));

        Assertions.assertDoesNotThrow(() -> fn.send(dispatch));

        Mockito.verify(PostListener2, times(1)).postSend(Mockito.any(), Mockito.any());
        // postSend should not interrupt the send.
        Mockito.verify(onException, never()).onException(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(mockProducer.jmsContext).close();
    }

    @Test
    void onexception_ex_01() throws JMSException {
        final var expected = new RuntimeException("preSend failed");
        final var ignored = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final var onExRef = new Exception[1];
        final DispatchListener.PreSend preSend = (d, m) -> {
            throw expected;
        };
        final DispatchListener.OnException onException1 = (d, m, e) -> {
            onExRef[0] = e;
            throw ignored;
        };
        final var onException2 = Mockito.mock(DispatchListener.OnException.class);

        final var mockProducer = newProducer();

        final var actual = Assertions
                .assertThrows(JmsDispatchFailedException.class, () -> new DefaultDispatchFn(mockProducer.conFactory,
                        toNullJson, List.of(onException1, preSend, onException2)).send(dispatch))
                .getCause();

        // onException is suppressed.
        Assertions.assertEquals(expected, actual, "should be from preSend");
        Assertions.assertEquals(expected, onExRef[0]);

        // All listeners should be called.
        Mockito.verify(onException2, times(1)).onException(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(mockProducer.jmsContext).close();
    }

    @Test
    void listener_01() {
        final var cf = Mockito.mock(ConnectionFactory.class);
        final var jmsContext = Mockito.mock(JMSContext.class);
        final var producer = Mockito.mock(JMSProducer.class);
        final var message = Mockito.mock(TextMessage.class);
        final var queue = Mockito.mock(Queue.class);

        final ArgumentMatcher<JmsMsg> matchMessage = msg -> msg.message() == message;

        Mockito.when(cf.createContext()).thenReturn(jmsContext);

        Mockito.when(jmsContext.createProducer()).thenReturn(producer);
        Mockito.when(jmsContext.createTextMessage()).thenReturn(message);
        Mockito.when(jmsContext.createQueue(Mockito.anyString())).thenReturn(queue);

        Mockito.when(producer.setDeliveryDelay(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.setTimeToLive(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.send(ArgumentMatchers.any(), ArgumentMatchers.<TextMessage>any())).thenReturn(producer);

        final var dispatch = new MockDispatch();

        new DefaultDispatchFn(cf, toNullJson, listeners).send(dispatch);

        Mockito.verify(onDispatch, times(2)).onDispatch(dispatch);
        Mockito.verify(preSend, times(2)).preSend(Mockito.eq(dispatch), Mockito.argThat(matchMessage));
        Mockito.verify(postSend, times(2)).postSend(Mockito.eq(dispatch), Mockito.argThat(matchMessage));
        Mockito.verify(onException, times(0)).onException(Mockito.eq(dispatch), Mockito.argThat(matchMessage),
                Mockito.any(Exception.class));
    }

    @Test
    void listener_ex_send_02() {
        final var cf = Mockito.mock(ConnectionFactory.class);
        Assertions.assertThrows(NullPointerException.class,
                () -> new DefaultDispatchFn(cf, toNullJson, listeners).send(null));

        // Null should be checked very early on.
        Mockito.verify(onDispatch, times(0)).onDispatch(null);
        Mockito.verify(preSend, times(0)).preSend(Mockito.eq(null), Mockito.any(JmsMsg.class));
        Mockito.verify(postSend, times(0)).postSend(Mockito.eq(null), Mockito.any(JmsMsg.class));
        Mockito.verify(onException, times(0)).onException(Mockito.eq(null), Mockito.any(JmsMsg.class),
                Mockito.any(Exception.class));
    }

    @Test
    void listener_ex_send_03() throws JMSException {
        final var jmsException = new JMSRuntimeException("");

        final var cf = Mockito.mock(ConnectionFactory.class);
        final var jmsContext = Mockito.mock(JMSContext.class);
        final var producer = Mockito.mock(JMSProducer.class);
        final var message = Mockito.mock(TextMessage.class);
        final var queue = Mockito.mock(Queue.class);
        final ArgumentMatcher<JmsMsg> matchMessage = msg -> msg.message() == message;

        Mockito.when(cf.createContext()).thenReturn(jmsContext);

        Mockito.when(jmsContext.createProducer()).thenReturn(producer);
        Mockito.when(jmsContext.createTextMessage()).thenReturn(message);
        Mockito.when(jmsContext.createQueue(Mockito.anyString())).thenReturn(queue);

        Mockito.when(producer.setDeliveryDelay(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.setTimeToLive(ArgumentMatchers.anyLong())).thenReturn(producer);
        Mockito.when(producer.send(ArgumentMatchers.any(), ArgumentMatchers.<TextMessage>any())).thenReturn(producer);

        Mockito.doThrow(jmsException).when(producer).send(Mockito.any(Destination.class), Mockito.eq(message));

        final var dispatch = new MockDispatch();

        Assertions.assertThrows(JmsDispatchFailedException.class,
                () -> new DefaultDispatchFn(cf, toNullJson, listeners).send(dispatch)).getCause();

        Mockito.verify(onDispatch, times(2)).onDispatch(dispatch);
        Mockito.verify(preSend, times(2)).preSend(Mockito.eq(dispatch), Mockito.argThat(matchMessage));
        Mockito.verify(postSend, times(0)).postSend(Mockito.eq(dispatch), Mockito.any(JmsMsg.class));
        Mockito.verify(onException, times(2)).onException(Mockito.eq(dispatch), Mockito.argThat(matchMessage),
                Mockito.eq(jmsException));
    }

    @Test
    void ttl_01() {
        final var mockProducer = newProducer();

        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public Duration ttl() {
                return Duration.parse("PT123S");
            }

        });

        verify(mockProducer.producer, times(1)).setTimeToLive(123000);
    }

    @Test
    void ttl_02() {
        final var mockProducer = newProducer();

        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public Duration ttl() {
                return null;
            }

        });

        verify(mockProducer.producer, times(1)).setTimeToLive(0);
    }

    @Test
    void delay_01() throws JMSException {
        final var mockProducer = newProducer();

        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public Duration delay() {
                return Duration.parse("PT123S");
            }

        });

        verify(mockProducer.producer, times(1)).setDeliveryDelay(123000);
    }

    @Test
    void delay_02() throws JMSException {
        final var mockProducer = newProducer();

        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public Duration delay() {
                return null;
            }

        });

        verify(mockProducer.producer, times(1)).setDeliveryDelay(0);
    }

    @Test
    void property_01() throws JMSException {
        final var i = Integer.valueOf(2);
        final var properties = Map.<String, Object>of("key1", "value1", "key2", i);

        final var mockProducer = newProducer();
        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public Map<String, Object> properties() {
                return properties;
            }

        });

        verify(mockProducer.message, times(1)).setObjectProperty("key1", "value1");
        verify(mockProducer.message, times(1)).setObjectProperty("key2", i);
    }

    @Test
    void correlationId_01() throws JMSException {
        final var mockProducer = newProducer();
        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public String correlationId() {
                return null;
            }

        });

        verify(mockProducer.message, times(1)).setJMSCorrelationID(null);
    }

    @Test
    void correlationId_02() throws JMSException {
        final var id = UUID.randomUUID().toString();

        final var mockProducer = newProducer();
        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public String correlationId() {
                return id;
            }

        });

        verify(mockProducer.message, times(1)).setJMSCorrelationID(id);
    }

    @Test
    void group_01() throws JMSException {
        final var mockProducer = newProducer();
        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public String groupId() {
                return null;
            }

        });

        verify(mockProducer.message, times(0)).setStringProperty(Mockito.eq("JMSXGroupID"), Mockito.anyString());
        verify(mockProducer.message, times(0)).setIntProperty(Mockito.eq("JMSXGroupSeq"), Mockito.anyInt());
    }

    @Test
    void group_02() throws JMSException {
        final var mockProducer = newProducer();
        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public String groupId() {
                return "";
            }

        });

        verify(mockProducer.message, times(0)).setStringProperty(Mockito.eq("JMSXGroupID"), Mockito.anyString());
        verify(mockProducer.message, times(0)).setIntProperty(Mockito.eq("JMSXGroupSeq"), Mockito.anyInt());
    }

    @Test
    void group_03() throws JMSException {
        final var id = UUID.randomUUID().toString();

        final var mockProducer = newProducer();
        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public String groupId() {
                return id;
            }

        });

        verify(mockProducer.message, times(1)).setStringProperty(Mockito.eq("JMSXGroupID"), Mockito.eq(id));
        verify(mockProducer.message, times(1)).setIntProperty(Mockito.eq("JMSXGroupSeq"), Mockito.eq(0));
    }

    @Test
    void group_04() throws JMSException {
        final var id = UUID.randomUUID().toString();

        final var mockProducer = newProducer();
        new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

            @Override
            public String groupId() {
                return id;
            }

            @Override
            public int groupSeq() {
                return -123;
            }

        });

        verify(mockProducer.message, times(1)).setStringProperty(Mockito.eq("JMSXGroupID"), Mockito.eq(id));
        verify(mockProducer.message, times(1)).setIntProperty(Mockito.eq("JMSXGroupSeq"), Mockito.eq(-123));
    }

    @Test
    void group_05() {
        final var mockProducer = newProducer();
        final var actual = Assertions.assertThrows(JmsDispatchFailedException.class,
                () -> new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

                    @Override
                    public Map<String, Object> properties() {
                        return Map.of("JMSXGroupID", UUID.randomUUID().toString());
                    }
                })).getCause();

        Assertions.assertEquals(IllegalArgumentException.class, actual.getClass());
    }

    @Test
    void group_06() {
        final var mockProducer = newProducer();
        final var actual = Assertions.assertThrows(JmsDispatchFailedException.class,
                () -> new DefaultDispatchFn(mockProducer.conFactory, toNullJson, null).send(new MockDispatch() {

                    @Override
                    public Map<String, Object> properties() {
                        return Map.of("JMSXGroupSeq", UUID.randomUUID().toString());
                    }
                })).getCause();

        Assertions.assertEquals(IllegalArgumentException.class, actual.getClass());
    }

    record MockProducer(ConnectionFactory conFactory, JMSContext jmsContext, JMSProducer producer, Message message,
            Destination destination, ArgumentMatcher<JmsMsg> matchMessage) {
    }
}
