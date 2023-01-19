package me.ehp246.broker.sb.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.context.ActiveProfiles;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfEnvironmentVariable(named = "me.ehp246.broker.sb", matches = "true")
@ActiveProfiles("local")
class SessionTest {
    @Autowired
    private SessionedQueue queue;

    @Autowired
    private JmsTemplate template;

    @Test
    void send() {
        queue.send("GroupId1");
    }

    @Test
    void sendTemplate() {
        template.convertAndSend("auf-jms.session", "", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(final Message jmsMessage) throws JMSException {
                jmsMessage.setStringProperty("JMSXGroupID", "GroupId1");
                return jmsMessage;
            }
        });
    }
}
