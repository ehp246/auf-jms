package me.ehp246.test.embedded.inbound.property;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, NamedStringCase.class, NamedIntCase.class,
        NamedIntegerCase.class }, properties = {
        "interceptor.name.null=" }, webEnvironment = WebEnvironment.NONE)
class PropertyTest {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private AtomicReference<CompletableFuture<PropertyCase>> ref;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ListableBeanFactory beanFactory;

    @BeforeEach
    void clear() {
        ref.set(new CompletableFuture<>());
    }

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setBooleanProperty("b1", false);
                msg.setJMSCorrelationID(UUID.randomUUID().toString());
                return msg;
            }
        });

        Assertions.assertEquals(true, ref.get().get().map.get("b1") instanceof Boolean);
    }

    @Test
    void log4jHeader_01() throws InterruptedException, ExecutionException {
        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setStringProperty(AufJmsConstants.LOG4J_THREAD_CONTEXT_HEADER_PREFIX + "b1",
                        UUID.randomUUID().toString());
                return msg;
            }
        });

        Assertions.assertEquals(true,
                ref.get().get().map.get(AufJmsConstants.LOG4J_THREAD_CONTEXT_HEADER_PREFIX + "b1") instanceof String);
    }

    @Test
    void failureInterceptor_01() {
        Assertions.assertEquals(null,
                beanFactory.getBean("inboundEndpoint-0", InboundEndpoint.class).invocationListener());

        Assertions.assertEquals(appConfig.inteceptor,
                beanFactory.getBean("inboundEndpoint-1", InboundEndpoint.class).invocationListener());

        Assertions.assertEquals(null,
                beanFactory.getBean("inboundEndpoint-2", InboundEndpoint.class).invocationListener());

        Assertions.assertEquals(appConfig.inteceptor,
                beanFactory.getBean("inboundEndpoint-3", InboundEndpoint.class).invocationListener());

        Assertions.assertEquals(null,
                beanFactory.getBean("inboundEndpoint-4", InboundEndpoint.class).invocationListener());
    }

    @Test
    void named_01() throws InterruptedException, ExecutionException {
        final var namedCase = this.beanFactory.getBean(NamedStringCase.class).reset();

        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("NamedStringCase");
                return msg;
            }
        });

        Assertions.assertEquals(null, namedCase.getValue());
    }

    @Test
    void named_02() throws InterruptedException, ExecutionException {
        final var namedCase = this.beanFactory.getBean(NamedStringCase.class).reset();
        final var expected = UUID.randomUUID().toString();

        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("NamedStringCase");
                msg.setObjectProperty("Named", expected);
                return msg;
            }
        });

        Assertions.assertEquals(expected, namedCase.getValue());
    }

    @Test
    void named_int_01() throws InterruptedException, ExecutionException {
        final var namedCase = this.beanFactory.getBean(NamedIntCase.class).reset();
        final var expected = 123;

        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("NamedIntCase");
                msg.setIntProperty("Named", expected);
                return msg;
            }
        });

        Assertions.assertEquals(expected, namedCase.getValue());
    }

    @Test
    void named_int_02() throws InterruptedException, ExecutionException {
        final var namedCase = this.beanFactory.getBean(NamedIntCase.class).reset();

        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("NamedIntCase");
                return msg;
            }
        });

        Assertions.assertEquals(0, namedCase.getValue());
    }

    @Test
    void named_integer_01() throws InterruptedException, ExecutionException {
        final var namedCase = this.beanFactory.getBean(NamedIntegerCase.class).reset();

        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("NamedIntegerCase");
                return msg;
            }
        });

        Assertions.assertEquals(null, namedCase.getValue());
    }

    @Test
    void named_integer_02() throws InterruptedException, ExecutionException {
        final var namedCase = this.beanFactory.getBean(NamedIntegerCase.class).reset();
        final var expected = 123;

        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("NamedIntegerCase");
                msg.setIntProperty("Named", expected);
                return msg;
            }
        });

        Assertions.assertEquals(expected, namedCase.getValue());
    }

    @Test
    void named_integer_03() throws InterruptedException, ExecutionException {
        final var namedCase = this.beanFactory.getBean(NamedIntegerCase.class).reset();
        final var expected = 123;

        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setJMSType("NamedIntegerCase");
                msg.setObjectProperty("Named", Integer.valueOf(expected));
                return msg;
            }
        });

        Assertions.assertEquals(expected, namedCase.getValue());
    }
}
