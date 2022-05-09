package me.ehp246.aufjms.integration.endpoint.property;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.endpoint.InvocationListener.FailedInterceptor;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableForJms({ @Inbound(@From(TestQueueListener.DESTINATION_NAME)),
        @Inbound(value = @From("q"), failedInvocationInterceptor = "failedInvocationInterceptor"),
        @Inbound(value = @From("q"), failedInvocationInterceptor = "${interceptor.name:}"),
        @Inbound(value = @From("q"), failedInvocationInterceptor = "${interceptor.name:failedInvocationInterceptor}"),
        @Inbound(value = @From("q"), failedInvocationInterceptor = "${interceptor.name.null:failedInvocationInterceptor}") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    final FailedInterceptor inteceptor = f -> {
    };

    @Bean
    public AtomicReference<CompletableFuture<PropertyCase>> ref() {
        return new AtomicReference<CompletableFuture<PropertyCase>>(new CompletableFuture<>());
    }

    @Bean
    public FailedInterceptor failedInvocationInterceptor() {
        return inteceptor;
    }
}
