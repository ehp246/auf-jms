package me.ehp246.aufjms.integration.endpoint.property;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableForJms(@Inbound(@From(TestQueueListener.DESTINATION_NAME)))
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<PropertyCase>> ref() {
        return new AtomicReference<CompletableFuture<PropertyCase>>(new CompletableFuture<>());
    }
}
