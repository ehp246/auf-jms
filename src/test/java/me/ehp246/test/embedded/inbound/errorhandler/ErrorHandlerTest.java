package me.ehp246.test.embedded.inbound.errorhandler;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, OnMsg.class }, properties = {
        "error.handler=uncaught" }, webEnvironment = WebEnvironment.NONE)
class ErrorHandlerTest {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var ref = appConfig.newRef();

        jmsTemplate.send("q", new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("OnMsg");
                return msg;
            }
        });

        Assertions.assertEquals(true, ref.get().get() instanceof NumberFormatException);
    }
}
