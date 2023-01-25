package me.ehp246.test.embedded.endpoint.topic.sub;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({
        @Inbound(value = @From(value = "TOPIC", type = DestinationType.TOPIC)) })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<Integer>> ref() {
        return new AtomicReference<CompletableFuture<Integer>>(new CompletableFuture<>());
    }
}
