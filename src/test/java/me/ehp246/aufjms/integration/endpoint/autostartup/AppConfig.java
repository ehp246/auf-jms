package me.ehp246.aufjms.integration.endpoint.autostartup;

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
@EnableForJms({
        @Inbound(value = @At(TestQueueListener.DESTINATION_NAME), autoStartup = "${startup}", name = "startup1"),
        @Inbound(value = @At(TestQueueListener.DESTINATION_NAME), name = "startup2"),
        @Inbound(value = @At(TestQueueListener.DESTINATION_NAME), autoStartup = "false", name = "startup3") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<Integer>> ref() {
        return new AtomicReference<CompletableFuture<Integer>>(new CompletableFuture<>());
    }
}
