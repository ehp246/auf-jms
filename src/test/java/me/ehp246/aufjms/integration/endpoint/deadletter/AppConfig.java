package me.ehp246.aufjms.integration.endpoint.deadletter;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.endpoint.DeadMsgConsumer;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({
        @Inbound(value = @From("ref1"), deadMsgConsumer = "ref1") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    public CompletableFuture<DeadMsg> ref1 = new CompletableFuture<>();
    public CompletableFuture<DeadMsg> ref2 = new CompletableFuture<>();
    
    @Bean("ref1")
    DeadMsgConsumer deadMsgConsumer1() {
        return (msg, ex) -> ref1.complete(new DeadMsg(msg, ex));
    }
}
