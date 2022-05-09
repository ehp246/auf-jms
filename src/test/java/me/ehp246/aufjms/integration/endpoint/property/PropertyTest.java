package me.ehp246.aufjms.integration.endpoint.property;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {
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

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        jmsTemplate.send(TestQueueListener.DESTINATION_NAME, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                final var msg = session.createTextMessage();
                msg.setBooleanProperty("b1", false);
                msg.setJMSCorrelationID(UUID.randomUUID().toString());
                return msg;
            }
        });

        Assertions.assertEquals(true, ref.get().get().map.get("b1") instanceof Boolean);
    }

    @Test
    void failureInterceptor_01() {
        Assertions.assertEquals(null,
                beanFactory.getBean("InboundEndpoint-0", InboundEndpoint.class).invocationListener());
        
        Assertions.assertEquals(appConfig.inteceptor,
                beanFactory.getBean("InboundEndpoint-1", InboundEndpoint.class).invocationListener());

        Assertions.assertEquals(null,
                beanFactory.getBean("InboundEndpoint-2", InboundEndpoint.class).invocationListener());

        Assertions.assertEquals(appConfig.inteceptor,
                beanFactory.getBean("InboundEndpoint-3", InboundEndpoint.class).invocationListener());

        Assertions.assertEquals(null,
                beanFactory.getBean("InboundEndpoint-4", InboundEndpoint.class).invocationListener());
    }
}
