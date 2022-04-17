package me.ehp246.aufjms.integration.endpoint.completed;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.endpoint.CompletedInvocation;
import me.ehp246.aufjms.api.endpoint.CompletedInvocationConsumer;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({
        @Inbound(value = @From("q1"), completedInvocationConsumer = "comp1") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    public static CompletableFuture<CompletedInvocation> comp1Ref = new CompletableFuture<>();

    @Bean
    CompletedInvocationConsumer comp1() {
        return completed -> {
            comp1Ref.complete(completed);
        };
    }
}
