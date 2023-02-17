package me.ehp246.aufjms.core.dispatch;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.jms.Destination;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
class DispatchLoggerTest {
    private final static JmsDispatch dispatch = new JmsDispatch() {
        @Override
        public At to() {
            return null;
        }
    };

    private final static JmsMsg msg = new JmsMsg() {

        @Override
        public String id() {
            return null;
        }

        @Override
        public Destination destination() {
            return null;
        }

        @Override
        public String type() {
            return null;
        }

        @Override
        public String correlationId() {
            return null;
        }

        @Override
        public String text() {
            return null;
        }

        @Override
        public Destination replyTo() {
            return null;
        }

        @Override
        public String groupId() {
            return null;
        }

        @Override
        public int groupSeq() {
            return 0;
        }

        @Override
        public boolean redelivered() {
            return false;
        }

        @Override
        public int deliveryCount() {
            return 0;
        }

        @Override
        public Instant expiration() {
            return null;
        }

        @Override
        public Instant timestamp() {
            return null;
        }

        @Override
        public String invoking() {
            return null;
        }

        @Override
        public <T> T property(final String name, final Class<T> type) {
            return null;
        }

        @Override
        public Set<String> propertyNames() {
            return null;
        }

        @Override
        public TextMessage message() {
            return null;
        }

    };

    @Test
    void ondispatch_01() {
        new DispatchLogger().onDispatch(null);
    }

    @Test
    void ondispatch_02() {
        new DispatchLogger().onDispatch(dispatch);
    }

    @Test
    void presend_01() {
        new DispatchLogger().preSend(null, null);

        new DispatchLogger().preSend(dispatch, msg);
    }

    @Test
    void postsend_01() {
        new DispatchLogger().postSend(null, null);

        new DispatchLogger().postSend(dispatch, msg);
    }

    @Test
    void onexception_01() {
        new DispatchLogger().onException(null, null, null);

        new DispatchLogger().onException(dispatch, msg, new Exception());
    }
}
