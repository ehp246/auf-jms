package me.ehp246.aufjms.integration.endpoint.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableForJms(@Inbound(@At(TestQueueListener.DESTINATION_NAME)))
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<Integer>> ref() {
        return new AtomicReference<CompletableFuture<Integer>>(new CompletableFuture<>());
    }
}
