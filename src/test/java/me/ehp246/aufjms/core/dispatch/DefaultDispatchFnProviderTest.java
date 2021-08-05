package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.util.MockDispatch;
import me.ehp246.aufjms.util.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
class DefaultDispatchFnProviderTest {
    @Mock
    private Destination destination;
    @Mock
    private Connection connection;
    @Mock
    private Session session;
    @Mock
    private MessageProducer producer;
    @Mock
    private TextMessage textMessage;

    @BeforeEach
    public void before() throws JMSException {
        Mockito.when(this.connection.createSession(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt()))
                .thenReturn(this.session);
        Mockito.when(this.session.createProducer(ArgumentMatchers.any())).thenReturn(this.producer);
        this.textMessage = new MockTextMessage();
        Mockito.when(this.session.createTextMessage()).thenReturn(this.textMessage);
        // Mockito.when(this.producer.send(ArgumentMatchers.any(),
        // ArgumentMatchers.eq(this.textMessage))''
    }

    @Test
    void listener_01() {
        final var count = new int[] { 0 };
        final var listener = new DispatchListener() {

            @Override
            public void onDispatch(JmsMsg msg) {
                count[0]++;
            }
        };
        new DefaultDispatchFnProvider(name -> connection, values -> null, List.of(listener, listener)).get("")
                .dispatch(new MockDispatch());

        Assertions.assertEquals(2, count[0]);
    }

    @Test
    void ttl_01() throws JMSException {
        new DefaultDispatchFnProvider(name -> connection, values -> null, List.of()).get("")
                .dispatch(new MockDispatch() {

                    @Override
                    public Duration ttl() {
                        return Duration.parse("PT123S");
                    }

                });

        verify(producer, times(1)).setTimeToLive(123000);
    }
}
