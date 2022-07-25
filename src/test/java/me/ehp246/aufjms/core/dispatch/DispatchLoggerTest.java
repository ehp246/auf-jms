package me.ehp246.aufjms.core.dispatch;

import java.time.Instant;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
class DispatchLoggerTest {

    @Test
    void presend_01() {
        new DispatchLogger().preSend(null, null);

        new DispatchLogger().preSend(new JmsDispatch() {

            @Override
            public At to() {
                // TODO Auto-generated method stub
                return null;
            }

        }, new JmsMsg() {

            @Override
            public String id() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Destination destination() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String type() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String correlationId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String text() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Destination replyTo() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String groupId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int groupSeq() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public boolean redelivered() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public int deliveryCount() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Instant expiration() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Instant timestamp() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String invoking() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T property(String name, Class<T> type) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Set<String> propertyNames() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public TextMessage message() {
                // TODO Auto-generated method stub
                return null;
            }

        });
    }

}
