package me.ehp246.aufjms.integration.endpoint.topic;

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
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestTopicListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableForJms({
        @Inbound(value = @From(value = TestTopicListener.DESTINATION_NAME, type = DestinationType.TOPIC, sub = @Sub(TestTopicListener.SUBSCRIPTION_NAME)), name = "sub1"),
        @Inbound(value = @From(value = TestTopicListener.DESTINATION_NAME, type = DestinationType.TOPIC, sub = @Sub(shared = false, durable = false)), name = "sub2"),
        @Inbound(value = @From(TestTopicListener.DESTINATION_NAME), name = "sub3"),
        @Inbound(value = @From(value = TestTopicListener.DESTINATION_NAME, type = DestinationType.TOPIC, sub = @Sub("${sub-name}")), name = "sub4") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<Integer>> ref() {
        return new AtomicReference<CompletableFuture<Integer>>(new CompletableFuture<>());
    }
}
