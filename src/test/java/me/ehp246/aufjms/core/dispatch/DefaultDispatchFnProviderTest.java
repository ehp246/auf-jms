package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.util.MockDispatch;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("resource")
@MockitoSettings(strictness = Strictness.WARN)
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

//    @Mock
//    private JMSContext ctx;
//    @Mock
//    private JMSProducer producer;

    private final ConnectionFactoryProvider cfProvder = name -> cf;

    @BeforeEach
    public void before() throws JMSException {
        Mockito.when(this.cf.createConnection()).thenReturn(this.conn);
        Mockito.when(this.conn.createSession()).thenReturn(this.session);
        Mockito.when(this.session.createProducer(null)).thenReturn(this.producer);
        Mockito.when(this.session.createTextMessage()).thenReturn(this.textMessage);

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
        final var count = new ArrayList<Object>();
        final var listener = new DispatchListener() {

            @Override
            public void onDispatch(JmsMsg msg, JmsDispatch dispatch) {
                count.add(msg);
                count.add(dispatch);
            }
        };
        final var dispatch = new MockDispatch();
        new DefaultDispatchFnProvider(cfProvder, values -> null, List.of(listener, listener)).get("")
                .send(dispatch);

        Assertions.assertEquals(count.get(0), count.get(2));
        Assertions.assertEquals(4, count.size());
        Assertions.assertEquals(count.get(1), count.get(3));
        Assertions.assertEquals(true, count.get(1) == dispatch);

    }

    @Test
    void send_01() throws JMSException {
        final var dispatchFn = new DefaultDispatchFnProvider(cfProvder, values -> null, null).get("");

        var jmsMsg = dispatchFn.send(new MockDispatch());

        verify(producer, times(1)).send(ArgumentMatchers.any(), ArgumentMatchers.eq(jmsMsg.message()));
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
    void cfname_001() {
        final var cfpMock = Mockito.mock(ConnectionFactoryProvider.class);
        when(cfpMock.get("")).thenReturn(this.cf);
        new DefaultDispatchFnProvider(cfpMock, values -> null, null).get("");

        verify(cfpMock, times(1)).get("");
    }
}
