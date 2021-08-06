package me.ehp246.aufjms.global.destination.case001;

import java.util.stream.Collectors;

import javax.jms.Queue;
import javax.jms.Topic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.endpoint.MsgEndpoint;
import me.ehp246.aufjms.api.jms.DestinationNameResolver;

@Disabled
@SpringBootTest(classes = DestinationConfiguration001.class, properties = {
        "spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false",
        "aufjms.reply.topic=topic.003", "aufjms.request.queue=queue.003", "aufjms.request.queue02=queue.003" })
class DestinationTest001 {
    @Autowired
    private ListableBeanFactory appCtx;

    @Test
    void destination001() {
        final var endpoints = appCtx.getBeansOfType(MsgEndpoint.class);

        Assertions.assertEquals(3, endpoints.size());

        final var names = endpoints.values().stream().map(MsgEndpoint::getDestinationName).collect(Collectors.toSet());

        Assertions.assertEquals(true, names.contains("topic://topic.003"));
        Assertions.assertEquals(true, names.contains("queue://queue.003"));

        final var resolver = appCtx.getBean(DestinationNameResolver.class);
        Assertions.assertEquals(true, resolver.resolve("", "topic://topic.003") instanceof Topic);
        Assertions.assertEquals(true, resolver.resolve("", "queue://queue.003") instanceof Queue);
        Assertions.assertEquals(true, resolver.resolve("", "queue.003") instanceof Queue);
        Assertions.assertEquals(resolver.resolve("", "queue.003").toString(),
                resolver.resolve("", "queue://queue.003").toString());
    }

}
