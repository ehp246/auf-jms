package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.util.MockDispatch;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("resource")
class DefaultDispatchFnProviderTest {
    @Mock
    private ConnectionFactory cf;
    @Mock
    private Destination destination;
    @Mock
    private Session session;
    @Mock
    private MessageProducer producer;
    @Mock
    private Connection conn;
    @Mock
    private TextMessage textMessage;
    @Mock
    private Queue queue;
    @Mock
    private Topic topic;
    @Mock
    private ConnectionFactoryProvider cfProvder;

    private final DispatchListener.OnDispatch onDispatch = Mockito.mock(DispatchListener.OnDispatch.class);
    private final DispatchListener.PreSend preSend = Mockito.mock(DispatchListener.PreSend.class);
    private final DispatchListener.PostSend postSend = Mockito.mock(DispatchListener.PostSend.class);
    private final DispatchListener.OnException onException = Mockito.mock(DispatchListener.OnException.class);

    private final List<DispatchListener> listeners = List.of(onDispatch, preSend, postSend, onException, onDispatch,
            preSend, postSend, onException);
    private final ArgumentMatcher<JmsMsg> matchMessage = msg -> msg.message() == textMessage;
    private final ArgumentMatcher<JmsMsg> matchNullMessage = msg -> msg == null;

    @BeforeEach
    void beforeAll() throws JMSException {
        Mockito.when(this.cfProvder.get("")).thenReturn(this.cf);
        Mockito.when(this.cf.createConnection()).thenReturn(this.conn);
        Mockito.when(this.conn.createSession()).thenReturn(this.session);
        Mockito.when(this.session.createProducer(null)).thenReturn(this.producer);
        Mockito.when(this.session.createTextMessage()).thenReturn(this.textMessage);
        Mockito.when(this.session.createQueue(Mockito.anyString())).thenReturn(this.queue);

        AufJmsContext.clearSession();

//        Mockito.when(this.cf.createContext(ArgumentMatchers.anyInt())).thenReturn(this.ctx);
//        Mockito.when(this.ctx.createContext(ArgumentMatchers.anyInt())).thenReturn(this.ctx);
//        Mockito.when(this.ctx.createTextMessage()).thenReturn(this.textMessage);
//        Mockito.when(this.ctx.createProducer()).thenReturn(this.producer);

//        Mockito.when(this.producer.setDeliveryDelay(ArgumentMatchers.anyLong())).thenReturn(this.producer);
//        Mockito.when(this.producer.setTimeToLive(ArgumentMatchers.anyLong())).thenReturn(this.producer);
//        Mockito.when(this.producer.send(ArgumentMatchers.any(), ArgumentMatchers.<TextMessage>any()))
//                .thenReturn(this.producer);
    }

    @Test
    void listener_01() {
        final var dispatch = new MockDispatch();

        new DefaultDispatchFnProvider(cfProvder, values -> null, listeners).get("").send(dispatch);

        Mockito.verify(onDispatch, times(2)).onDispatch(dispatch);
        Mockito.verify(preSend, times(2)).preSend(Mockito.eq(dispatch), Mockito.argThat(matchMessage));
        Mockito.verify(postSend, times(2)).postSend(Mockito.eq(dispatch), Mockito.argThat(matchMessage));
        Mockito.verify(onException, times(0)).onException(Mockito.eq(dispatch), Mockito.argThat(matchMessage),
                Mockito.any(Exception.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void listener_02() throws JMSException {
        final var jmsException = new JMSException("");
        Mockito.doThrow(jmsException).when(this.conn).createSession();

        final var dispatch = new MockDispatch();

        final var thrown = Assertions.assertThrows(JMSRuntimeException.class,
                () -> new DefaultDispatchFnProvider(cfProvder, values -> null, listeners).get("").send(dispatch));

        Mockito.verify(onDispatch, times(2)).onDispatch(dispatch);
        Mockito.verify(preSend, times(0)).preSend(Mockito.eq(dispatch), Mockito.any(JmsMsg.class));
        Mockito.verify(postSend, times(0)).postSend(Mockito.eq(dispatch), Mockito.any(JmsMsg.class));
        Mockito.verify(onException, times(2)).onException(Mockito.eq(dispatch), Mockito.eq(null),
                Mockito.eq(jmsException));

        Assertions.assertEquals(jmsException, thrown.getCause());
    }

    @Test
    void listener_03() throws JMSException {
        final var jmsException = new JMSException("");
        Mockito.doThrow(jmsException).when(this.producer).send(Mockito.any(Destination.class), Mockito.eq(textMessage));

        final var dispatch = new MockDispatch();

        Assertions.assertThrows(JMSRuntimeException.class,
                () -> new DefaultDispatchFnProvider(cfProvder, values -> null, listeners).get("").send(dispatch));

        Mockito.verify(onDispatch, times(2)).onDispatch(dispatch);
        Mockito.verify(preSend, times(2)).preSend(Mockito.eq(dispatch), Mockito.argThat(matchMessage));
        Mockito.verify(postSend, times(0)).postSend(Mockito.eq(dispatch), Mockito.any(JmsMsg.class));
        Mockito.verify(onException, times(2)).onException(Mockito.eq(dispatch), Mockito.argThat(matchMessage),
                Mockito.eq(jmsException));
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void listener_04() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new DefaultDispatchFnProvider(cfProvder, values -> null, listeners).get("").send(null));

        // Null should be checked very early on.
        Mockito.verify(onDispatch, times(0)).onDispatch(null);
        Mockito.verify(preSend, times(0)).preSend(Mockito.eq(null), Mockito.any(JmsMsg.class));
        Mockito.verify(postSend, times(0)).postSend(Mockito.eq(null), Mockito.any(JmsMsg.class));
        Mockito.verify(onException, times(0)).onException(Mockito.eq(null), Mockito.any(JmsMsg.class),
                Mockito.any(Exception.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void listener_05() {
        final var listener = Mockito.mock(DispatchListener.OnDispatch.class, withSettings().extraInterfaces(
                DispatchListener.PreSend.class, DispatchListener.PostSend.class, DispatchListener.OnException.class));

        final var expected = new MockDispatch();

        new DefaultDispatchFnProvider(cfProvder, v -> null, List.of(listener)).get("").send(expected);

        // Null should be checked very early on.
        Mockito.verify(listener, times(1)).onDispatch(expected);
        Mockito.verify((DispatchListener.PreSend) listener, times(1)).preSend(Mockito.eq(expected),
                Mockito.any(JmsMsg.class));
        Mockito.verify((DispatchListener.PostSend) listener, times(1)).postSend(Mockito.eq(expected),
                Mockito.any(JmsMsg.class));
        Mockito.verify((DispatchListener.OnException) listener, times(0)).onException(Mockito.any(JmsDispatch.class),
                Mockito.any(JmsMsg.class), Mockito.any(Exception.class));
    }

    @Test
    void send_01() throws JMSException {
        final var dispatchFn = new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("");

        final var jmsMsg = dispatchFn.send(new MockDispatch());

        Mockito.verify(producer).send(ArgumentMatchers.eq(queue), ArgumentMatchers.eq(jmsMsg.message()));
        // Should clean up everything.
        Mockito.verify(producer).close();
        Mockito.verify(session).close();
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void ex_01() throws JMSException {
        final var jmsException = new JMSException("");

        Mockito.doThrow(jmsException).when(this.producer).setTimeToLive(ArgumentMatchers.anyLong());

        final var dispatchFn = new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("");

        final var actual = Assertions.assertThrows(RuntimeException.class, () -> dispatchFn.send(new MockDispatch()));

        Assertions.assertEquals(jmsException, actual.getCause());

        // Should clean up everything.
        Mockito.verify(producer).close();
        Mockito.verify(session).close();
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void ex_02() throws JMSException {
        // Should allow to create.
        final var fn = new DefaultDispatchFnProvider(name -> null, values -> null, null).get(null);

        // Should throw without connection and context session.
        Assertions.assertThrows(NullPointerException.class, () -> fn.send(null));

        Mockito.verify(conn, times(0)).createSession();
        Mockito.verify(session, times(0)).createProducer(null);
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void ex_ondispatch_01() throws JMSException {
        final var expected = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final DispatchListener.OnDispatch listener = d -> {
            throw expected;
        };

        final var fn = new DefaultDispatchFnProvider(cfProvder, values -> null, List.of(listener, onException)).get("");

        final var actual = Assertions.assertThrows(IllegalArgumentException.class, () -> fn.send(dispatch));

        Mockito.verify(onException).onException(Mockito.eq(dispatch), Mockito.argThat(matchNullMessage),
                Mockito.eq(expected));
        Mockito.verify(conn, times(0)).createSession();
        Mockito.verify(session, times(0)).createProducer(null);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void ex_presend_02() throws JMSException {
        final var expected = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final DispatchListener.PreSend listener = (d, m) -> {
            throw expected;
        };

        final var fn = new DefaultDispatchFnProvider(cfProvder, values -> null, List.of(listener, onException)).get("");

        final var actual = Assertions.assertThrows(IllegalArgumentException.class, () -> fn.send(dispatch));

        Mockito.verify(onException).onException(Mockito.eq(dispatch), Mockito.argThat(matchMessage),
                Mockito.eq(expected));
        Mockito.verify(producer).close();
        Mockito.verify(session).close();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void ex_postsend_03() throws JMSException {
        final var expected = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final DispatchListener.PostSend listener = (d, m) -> {
            throw expected;
        };

        final var fn = new DefaultDispatchFnProvider(cfProvder, values -> null, List.of(listener, onException)).get("");

        final var actual = Assertions.assertThrows(IllegalArgumentException.class, () -> fn.send(dispatch));

        Mockito.verify(onException).onException(Mockito.eq(dispatch), Mockito.argThat(matchMessage),
                Mockito.eq(expected));
        Mockito.verify(producer).close();
        Mockito.verify(session).close();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void ex_onexception_01() throws JMSException {
        final var initialCause = new RuntimeException("postSend failed");
        final var expected = new IllegalArgumentException();
        final var dispatch = new MockDispatch();
        final DispatchListener.PostSend postSend = (d, m) -> {
            throw initialCause;
        };
        final DispatchListener.OnException listener = (d, m, e) -> {
            throw expected;
        };

        final var fn = new DefaultDispatchFnProvider(cfProvder, values -> null, List.of(listener, postSend)).get("");

        final var actual = Assertions.assertThrows(IllegalArgumentException.class, () -> fn.send(dispatch));

        Mockito.verify(producer).close();
        Mockito.verify(session).close();

        Assertions.assertEquals(expected, actual, "should be re-thrown as is");
    }

    @Test
    void ttl_01() throws JMSException {
        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("").send(new MockDispatch() {

            @Override
            public Duration ttl() {
                return Duration.parse("PT123S");
            }

        });

        verify(producer, times(1)).setTimeToLive(123000);
    }

    @Test
    void ttl_02() throws JMSException {
        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("").send(new MockDispatch() {

            @Override
            public Duration ttl() {
                return null;
            }

        });

        verify(producer, times(1)).setTimeToLive(0);
    }

    @Test
    void delay_01() throws JMSException {
        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("").send(new MockDispatch() {

            @Override
            public Duration delay() {
                return Duration.parse("PT123S");
            }

        });

        verify(producer, times(1)).setDeliveryDelay(123000);
    }

    @Test
    void delay_02() throws JMSException {
        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("").send(new MockDispatch() {

            @Override
            public Duration delay() {
                return null;
            }

        });

        verify(producer, times(1)).setDeliveryDelay(0);
    }

    @Test
    void property_01() throws JMSException {
        final var i = Integer.valueOf(2);
        final var properties = Map.<String, Object>of("key1", "value1", "key2", i);

        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("").send(new MockDispatch() {

            @Override
            public Map<String, Object> properties() {
                return properties;
            }

        });

        verify(textMessage, times(1)).setObjectProperty("key1", "value1");
        verify(textMessage, times(1)).setObjectProperty("key2", i);
    }

    @Test
    void correlationId_01() throws JMSException {
        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("").send(new MockDispatch() {

            @Override
            public String correlationId() {
                return null;
            }

        });

        verify(textMessage, times(1)).setJMSCorrelationID(null);
    }

    @Test
    void correlationId_02() throws JMSException {
        final var id = UUID.randomUUID().toString();
        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("").send(new MockDispatch() {

            @Override
            public String correlationId() {
                return id;
            }

        });

        verify(textMessage, times(1)).setJMSCorrelationID(id);
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void cfname_01() {
        new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("");

        verify(cfProvder, times(1)).get("");
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void cfname_02() {
        final var cfpMock = Mockito.mock(ConnectionFactoryProvider.class);
        when(cfpMock.get("")).thenReturn(null);

        Assertions.assertThrows(Exception.class,
                () -> new DefaultDispatchFnProvider(cfpMock, values -> null, null).get(""));
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void close_01() throws JMSException {
        final var cfpMock = Mockito.mock(ConnectionFactoryProvider.class);
        when(cfpMock.get("")).thenReturn(this.cf);

        final var fnProvider = new DefaultDispatchFnProvider(cfpMock, values -> null, null);

        fnProvider.get("");

        // Closing the provider should close the connections.
        fnProvider.close();

        Mockito.verify(this.conn).close();
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void conn_01() {
        // Should allow to create.
        final var fn = new DefaultDispatchFnProvider(name -> null, values -> null, null).get(null);

        // Should throw without connection and context session.
        Assertions.assertThrows(NullPointerException.class, () -> fn.send(Mockito.mock(JmsDispatch.class)));
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void context_01() throws JMSException {
        // Should allow to create.
        final var fn = new DefaultDispatchFnProvider(name -> null, values -> null, null).get(null);

        final var mockSession = Mockito.mock(Session.class);
        Mockito.when(mockSession.createProducer(null)).thenReturn(this.producer);
        Mockito.when(mockSession.createTextMessage()).thenReturn(this.textMessage);

        AufJmsContext.set(mockSession);

        fn.send(new MockDispatch());

        // Should use the context session.
        Mockito.verify(mockSession).createProducer(null);
        Mockito.verify(producer).close();
        // Should not close the context session.
        Mockito.verify(session, times(0)).close();
    }

    @Test
    void context_02() throws JMSException {
        // Should create one with a connection.
        final var fn = new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("");

        final var ctxSession = Mockito.mock(Session.class);
        AufJmsContext.set(ctxSession);

        fn.send(new MockDispatch());

        // Should use the session from the connection.
        Mockito.verify(this.session).createProducer(null);

        // Should not use the context.
        Mockito.verify(ctxSession, times(0)).createProducer(null);
        // Should clean up everything.
        Mockito.verify(producer).close();
        Mockito.verify(session).close();
    }
}
