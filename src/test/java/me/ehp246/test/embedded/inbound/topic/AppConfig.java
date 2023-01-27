package me.ehp246.test.embedded.inbound.topic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From.Sub;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestTopicListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableForJms({
        @Inbound(value = @From(value = TestTopicListener.DESTINATION_NAME, type = DestinationType.TOPIC, sub = @Sub(name = TestTopicListener.SUBSCRIPTION_NAME)), name = "sub1"),
        @Inbound(value = @From(value = "T2", type = DestinationType.TOPIC)),
        @Inbound(value = @From(TestTopicListener.DESTINATION_NAME), name = "sub3"),
        @Inbound(value = @From(value = TestTopicListener.DESTINATION_NAME, type = DestinationType.TOPIC, sub = @Sub(durable = true, shared = true, name = "${sub-name}")), name = "sub4") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<Integer>> ref() {
        return new AtomicReference<CompletableFuture<Integer>>(new CompletableFuture<>());
    }
}
