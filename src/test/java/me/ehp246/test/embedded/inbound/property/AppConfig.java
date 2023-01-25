package me.ehp246.test.embedded.inbound.property;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnFailed;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableForJms({ @Inbound(@From(TestQueueListener.DESTINATION_NAME)),
        @Inbound(value = @From("q"), invocationListener = "failedInvocationInterceptor"),
        @Inbound(value = @From("q"), invocationListener = "${interceptor.name:}"),
        @Inbound(value = @From("q"), invocationListener = "${interceptor.name:failedInvocationInterceptor}"),
        @Inbound(value = @From("q"), invocationListener = "${interceptor.name.null:failedInvocationInterceptor}") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    final OnFailed inteceptor = f -> {
    };

    @Bean
    public AtomicReference<CompletableFuture<PropertyCase>> ref() {
        return new AtomicReference<CompletableFuture<PropertyCase>>(new CompletableFuture<>());
    }

    @Bean
    public OnFailed failedInvocationInterceptor() {
        return inteceptor;
    }
}
