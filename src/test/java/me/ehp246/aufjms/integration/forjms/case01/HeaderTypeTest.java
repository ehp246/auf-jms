package me.ehp246.aufjms.integration.forjms.case01;

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

import me.ehp246.aufjms.util.UtilConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {
        "spring.activemq.broker-url=vm://localhost?broker.persistent=false&broker.useShutdownHook=false" })
class HeaderTypeTest {
    @Autowired
    private AtomicReference<CompletableFuture<Integer>> ref;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    void type_01() throws InterruptedException, ExecutionException {
        final var i = (int) (Math.random() * 100);
        jmsTemplate.send(UtilConfig.TEST_QUEUE, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setText("" + i);
                return msg;
            }
        });

        Assertions.assertEquals(i, ref.get().get());
    }
}
