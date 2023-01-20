package me.ehp246.test.embedded.endpoint.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {})
class TypeTest {
    @Autowired
    private AtomicReference<CompletableFuture<Integer>> ref;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Unmatched umatcher;

    @Test
    void type_01() throws InterruptedException, ExecutionException {
        final var i = (int) (Math.random() * 100);
        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("Add");
                msg.setText("" + i);
                return msg;
            }
        });

        Assertions.assertEquals(i, ref.get().get());
    }

    @Test
    void type_02() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();
        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType(id);
                return msg;
            }
        });

        Assertions.assertEquals(id, umatcher.ref.get().get().type());
    }
}
