package me.ehp246.test.embedded.inbound.errorhandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.util.ErrorHandler;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 *
 */
@EnableJms
@EnableForJms({ @Inbound(value = @From("q"), errorHandler = "${error.handler}") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    private final AtomicReference<CompletableFuture<Throwable>> ref = new AtomicReference<CompletableFuture<Throwable>>();

    AtomicReference<CompletableFuture<Throwable>> newRef() {
        this.ref.set(new CompletableFuture<Throwable>());
        return this.ref;
    }

    @Bean
    ErrorHandler uncaught() {
        return t -> AppConfig.this.ref.get().complete(t);
    }
}
