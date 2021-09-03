package me.ehp246.aufjms.integration.endpoint.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import me.ehp246.aufjms.util.TestQueueListener;

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

    @Test
    void type_01() throws InterruptedException, ExecutionException {
        final var i = (int) (Math.random() * 100);
        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("Add");
                msg.setText("" + i);
                return msg;
            }
        });

        Assertions.assertEquals(i, ref.get().get());
    }
}
