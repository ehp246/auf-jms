package me.ehp246.test.embedded.inbound.completed;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({
        @Inbound(value = @From("q1"), invocationListener = "${comp1.name:}") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    public static CompletableFuture<Completed> comp1Ref = new CompletableFuture<>();

    @Bean
    OnCompleted comp1() {
        return completed -> {
            comp1Ref.complete(completed);
        };
    }
}
