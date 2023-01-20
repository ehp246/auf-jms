package me.ehp246.test.embedded.endpoint.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableForJms(value = @Inbound(value = @From(TestQueueListener.DESTINATION_NAME)), defaultConsumer = "unmatched")
@Import({ EmbeddedArtemisConfig.class })
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<Integer>> ref() {
        return new AtomicReference<CompletableFuture<Integer>>(new CompletableFuture<>());
    }

    @Bean
    public Unmatched unmatched() {
        return new Unmatched();
    }
}
