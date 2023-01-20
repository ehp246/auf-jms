package me.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocableTypeDefinition;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;
import me.ehp246.test.TestUtil;
import me.ehp246.test.TimingExtension;
import me.ehp246.test.mock.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
class DefaultInboundMessageListenerTest {
    private final static int LOOP = 1_000_000;

    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.perf", matches = "true")
    void perf_01() {
        Configurator.setLevel(LogManager.getLogger(InboundEndpoint.class).getName(), Level.INFO);

        final var binder = new DefaultInvocableBinder(new JsonByJackson(TestUtil.OBJECT_MAPPER));
        final var dispatcher = new DefaultInvocableDispatcher(binder, null, null);
        final var factory = new AutowireCapableInvocableFactory(new DefaultListableBeanFactory(),
                new DefaultInvocableRegistry().register(
                        List.of(new InvocableTypeDefinition(Set.of("PerfCase"),
                                InvocableBinderTestCases.PerfCase.class,
                                Map.of("", new ReflectedType<>(InvocableBinderTestCases.PerfCase.class)
                                        .findMethods("m01").get(0)),
                                InstanceScope.MESSAGE, InvocationModel.DEFAULT))
                                .stream()));
        final var listener = new DefaultInboundMessageListener(dispatcher, factory, null);

        final var msg = new MockTextMessage("PerfCase");
        final var session = Mockito.mock(Session.class);

        IntStream.range(0, LOOP).forEach(i -> {
            try {
                listener.onMessage(msg, session);
            } catch (final JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
