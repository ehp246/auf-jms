package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
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
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.util.MockDispatch;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
class DefaultDispatchFnProviderTest {
    @Mock
    private Destination destination;
    @Mock
    private JMSContext ctx;
    @Mock
    private JMSProducer producer;
    @Mock
    private TextMessage textMessage;

    @BeforeEach
    public void before() throws JMSException {
        Mockito.when(this.ctx.createTextMessage()).thenReturn(this.textMessage);
        Mockito.when(this.ctx.createProducer()).thenReturn(this.producer);
        Mockito.when(this.producer.setTimeToLive(ArgumentMatchers.anyLong())).thenReturn(this.producer);
        Mockito.when(this.producer.send(ArgumentMatchers.any(), ArgumentMatchers.<TextMessage>any()))
                .thenReturn(this.producer);
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
        new DefaultDispatchFnProvider(name -> ctx, values -> null, List.of(listener, listener)).get("")
                .dispatch(dispatch);

        Assertions.assertEquals(count.get(0), count.get(2));
        Assertions.assertEquals(4, count.size());
        Assertions.assertEquals(count.get(1), count.get(3));
        Assertions.assertEquals(true, count.get(1) == dispatch);

    }

    @Test
    void ttl_01() throws JMSException {
        new DefaultDispatchFnProvider(name -> ctx, values -> null, List.of()).get("").dispatch(new MockDispatch() {

            @Override
            public Duration ttl() {
                return Duration.parse("PT123S");
            }

        });

        verify(producer, times(1)).setTimeToLive(123000);
    }
}
